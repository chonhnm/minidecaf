package minidecaf;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MainVisitor extends MiniDecafBaseVisitor<Object> {

    private final StringBuilder sb;
    private final List<String> irList;
    private Map<String, LocalVar> localVarMap;
    private int ifLabelIndex;

    public MainVisitor(StringBuilder sb) {
        this.sb = sb;
        irList = new ArrayList<>();
    }

    @Override
    public Object visitProg(MiniDecafParser.ProgContext ctx) {
        sb.append("\t.text\n\t.globl\tmain\n");
        super.visitProg(ctx);
        return null;
    }

    @Override
    public Object visitFunc(MiniDecafParser.FuncContext ctx) {
        String text = ctx.ident.getText();
        if (!"main".equals(text)) {
            throw new RuntimeException("function name is not main.");
        }
        sb.append(text).append(":\n");
        localVarMap = new HashMap<>();
        ifLabelIndex = 0;
        visitChildren(ctx);
        transIR(text);
        return null;
    }

    @Override
    public Object visitRetStat(MiniDecafParser.RetStatContext ctx) {
        visit(ctx.expr());
        irList.add("ret");
        return null;
    }

    @Override
    public Object visitExprStat(MiniDecafParser.ExprStatContext ctx) {
        visitChildren(ctx);
        irList.add("pop");
        return null;
    }

    @Override
    public Object visitIfState(MiniDecafParser.IfStateContext ctx) {
        int childCount = ctx.getChildCount();
        int idx = ifLabelIndex++;
        if (childCount > 1) {
            MiniDecafParser.IfWithoutElseContext thenChild = ctx.ifWithoutElse();
            MiniDecafParser.StatContext elseChild = ctx.stat();
            visit(thenChild.expr());
            irList.add("beqz ELSE_LABEL" + idx);
            visit(thenChild.stat());
            irList.add("br END_LABEL" + idx);
            irList.add("label ELSE_LABEL" + idx);
            visit(elseChild);
            irList.add("label END_LABEL" + idx);
        } else {
            MiniDecafParser.IfWithoutElseContext child = ctx.ifWithoutElse();
            visit(child.expr());
            irList.add("beqz END_LABEL" + idx);
            visit(child.stat());
            irList.add("label END_LABEL" + idx);
        }
        return null;
    }

    @Override
    public Object visitDeclaration(MiniDecafParser.DeclarationContext ctx) {
        MiniDecafParser.ExprContext expr = ctx.expr();
        if (expr == null) {
            irList.add("push 0");
        } else {
            visit(expr);
        }
        TerminalNode ident = ctx.Identifier();
        Token symbol = ident.getSymbol();
        int line = symbol.getLine();
        int col = symbol.getCharPositionInLine();
        String text = ident.getText();
        LocalVar localVar = localVarMap.get(text);
        if (localVar != null) {
            throw new ParserException("Declaration error: variable " + text + " has declared.", line, col);
        }
        int size = localVarMap.size();
        localVar = new LocalVar(text, size, line, col);
        localVarMap.put(text, localVar);
        irList.add("frameaddr " + size);
        irList.add("store");
        irList.add("pop");
        return null;
    }

    @Override
    public Object visitAssignment(MiniDecafParser.AssignmentContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.expr());
            TerminalNode identifier = ctx.Identifier();
            String text = identifier.getText();
            LocalVar localVar = localVarMap.get(text);
            if (localVar == null) {
                Token symbol = identifier.getSymbol();
                int line = symbol.getLine();
                int col = symbol.getCharPositionInLine();
                throw new ParserException("Assignment error: variable " + text + " has not declared.", line, col);
            }
            irList.add("frameaddr " + localVar.index);
            irList.add("store");
        } else {
            visit(ctx.conditional());
        }
        return null;
    }

    @Override
    public Object visitConditional(MiniDecafParser.ConditionalContext ctx) {
        int idx = ifLabelIndex++;
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.logical_or());
            irList.add("beqz ELSE_LABEL" + idx);
            visit(ctx.expr());
            irList.add("br END_LABEL" + idx);
            irList.add("label ELSE_LABEL" + idx);
            visit(ctx.conditional());
            irList.add("label END_LABEL" + idx);
        } else {
            visit(ctx.logical_or());
        }
        return null;
    }

    @Override
    public Object visitLogical_or(MiniDecafParser.Logical_orContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.logical_or());
            visit(ctx.logical_and());
            irList.add("lor");
        } else {
            visit(ctx.logical_and());
        }
        return null;
    }

    @Override
    public Object visitLogical_and(MiniDecafParser.Logical_andContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.logical_and());
            visit(ctx.equality());
            irList.add("land");
        } else {
            visit(ctx.equality());
        }
        return null;
    }

    @Override
    public Object visitEquality(MiniDecafParser.EqualityContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.equality());
            visit(ctx.relational());
            String opText = ctx.op.getText();
            opText = "==".equals(opText) ? "eq" : "ne";
            irList.add(opText);
        } else {
            visit(ctx.relational());
        }
        return null;
    }

    @Override
    public Object visitRelational(MiniDecafParser.RelationalContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.relational());
            visit(ctx.additive());
            String opText = ctx.op.getText();
            opText = "<".equals(opText) ? "lt"
                    : ">".equals(opText) ? "gt"
                    : "<=".equals(opText) ? "le"
                    : "ge";
            irList.add(opText);
        } else {
            visit(ctx.additive());
        }
        return null;
    }

    @Override
    public Object visitAdditive(MiniDecafParser.AdditiveContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.additive());
            visit(ctx.multiplicative());
            String opText = ctx.op.getText();
            opText = "+".equals(opText) ? "add" : "sub";
            irList.add(opText);
        } else {
            visit(ctx.multiplicative());
        }
        return null;
    }

    @Override
    public Object visitMultiplicative(MiniDecafParser.MultiplicativeContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.multiplicative());
            visit(ctx.unary());
            String opText = ctx.op.getText();
            opText = "*".equals(opText) ? "mul" :
                    "/".equals(opText) ? "div" : "rem";
            irList.add(opText);
        } else {
            visit(ctx.unary());
        }
        return null;
    }

    @Override
    public Object visitUnary(MiniDecafParser.UnaryContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.unary());
            String opText = ctx.op.getText();
            opText = "-".equals(opText) ? "neg" :
                    "~".equals(opText) ? "not" : "lnot";
            irList.add(opText);
        } else {
            visit(ctx.primary());
        }
        return null;
    }

    @Override
    public Object visitPriConst(MiniDecafParser.PriConstContext ctx) {
        String text = ctx.getText();
        irList.add("push " + Integer.parseInt(text));
        return null;
    }

    @Override
    public Object visitPriIdent(MiniDecafParser.PriIdentContext ctx) {
        TerminalNode identifier = ctx.Identifier();
        String text = identifier.getText();
        LocalVar localVar = localVarMap.get(text);
        if (localVar == null) {
            Token symbol = identifier.getSymbol();
            int line = symbol.getLine();
            int col = symbol.getCharPositionInLine();
            throw new ParserException("Read error: variable " + text + " has not defined.", line, col);
        }
        irList.add("frameaddr " + localVar.index);
        irList.add("load");
        return null;
    }

    private void transIR(String funcName) {
        prologue();
        // if no ret, then return 0
        irList.add("push 0");
        for (String opStr : irList) {
            String[] split = opStr.split(" ");
            String op = split[0];
            String val;
            switch (op) {
                case "lor":
                    pop("t2");
                    pop("t1");
                    sb.append("\tor t1,t1,t2\n")
                            .append("\tsnez t1,t1\n");
                    push("t1");
                    break;
                case "land":
                    pop("t2");
                    pop("t1");
                    sb.append("\tsnez t1,t1\n")
                            .append("\tsnez t2,t2\n")
                            .append("\tand t1,t1,t2\n");
                    push("t1");
                    break;
                case "eq":
                    pop("t2");
                    pop("t1");
                    sb.append("\tsub t1,t1,t2\n")
                            .append("\tseqz t1,t1\n");
                    push("t1");
                    break;
                case "ne":
                    pop("t2");
                    pop("t1");
                    sb.append("\tsub t1,t1,t2\n")
                            .append("\tsnez t1,t1\n");
                    push("t1");
                    break;
                case "le":
                    pop("t2");
                    pop("t1");
                    sb.append("\tsgt t1,t1,t2\n")
                            .append("\txori t1,t1,1\n");
                    push("t1");
                    break;
                case "ge":
                    pop("t2");
                    pop("t1");
                    sb.append("\tslt t1,t1,t2\n")
                            .append("\txori t1,t1,1\n");
                    push("t1");
                    break;
                case "lt":
                    pop("t2");
                    pop("t1");
                    sb.append("\tslt t1,t1,t2\n");
                    push("t1");
                    break;
                case "gt":
                    pop("t2");
                    pop("t1");
                    sb.append("\tsgt t1,t1,t2\n");
                    push("t1");
                    break;
                case "neg":
                    pop("t1");
                    sb.append("\tneg t1,t1\n");
                    push("t1");
                    break;
                case "not":
                    pop("t1");
                    sb.append("\tnot t1,t1\n");
                    push("t1");
                    break;
                case "lnot":
                    pop("t1");
                    sb.append("\tseqz t1,t1\n");
                    push("t1");
                    break;
                case "add":
                    pop("t2"); // rvalue
                    pop("t1"); // lvalue
                    sb.append("\tadd t1,t1,t2\n");
                    push("t1");
                    break;
                case "sub":
                    pop("t2"); // rvalue
                    pop("t1"); // lvalue
                    sb.append("\tsub t1,t1,t2\n");
                    push("t1");
                    break;
                case "mul":
                    pop("t2"); // rvalue
                    pop("t1"); // lvalue
                    sb.append("\tmul t1,t1,t2\n");
                    push("t1");
                    break;
                case "div":
                    pop("t2"); // rvalue
                    pop("t1"); // lvalue
                    sb.append("\tdiv t1,t1,t2\n");
                    push("t1");
                    break;
                case "rem":
                    pop("t2"); // rvalue
                    pop("t1"); // lvalue
                    sb.append("\trem t1,t1,t2\n");
                    push("t1");
                    break;
                case "push":
                    val = split[1];
                    sb.append("\tli t1,").append(val).append("\n");
                    push("t1");
                    break;
                case "frameaddr":
                    val = split[1];
                    int k = Integer.parseInt(val);
                    sb.append("\taddi sp, sp, -4\n");
                    sb.append("\taddi t1, fp, ").append(-12-4*k).append("\n");
                    sb.append("\tsw t1, 0(sp)\n");
                    break;
                case "load":
                    sb.append("\tlw t1, 0(sp)\n");
                    sb.append("\tlw t1, 0(t1)\n");
                    sb.append("\tsw t1, 0(sp)\n");
                    break;
                case "store":
                    sb.append("\tlw t1, 4(sp)\n");
                    sb.append("\tlw t2, 0(sp)\n");
                    sb.append("\taddi sp, sp, 4\n");
                    sb.append("\tsw t1, 0(t2)\n");
                    break;
                case "pop":
                    sb.append("\taddi sp, sp, 4\n");
                    break;
                case "ret":
                    sb.append("\tjal t1, ").append(funcName).append("_epilogue\n");
                    break;
                case "label":
                    val = split[1];
                    sb.append("\t").append(val).append(":\n");
                    break;
                case "beqz":
                    val = split[1];
                    sb.append("\tlw t1, 0(sp)\n");
                    sb.append("\taddi sp, sp, 4\n");
                    sb.append("\tbeqz t1, ").append(val).append("\n");
                    break;
                case "bnez":
                    val = split[1];
                    sb.append("\tlw t1, 0(sp)\n");
                    sb.append("\taddi sp, sp, 4\n");
                    sb.append("\tbnez t1, ").append(val).append("\n");
                    break;
                case "br":
                    val = split[1];
                    sb.append("\tj ").append(val).append("\n");
                    break;
            }
        }
        epilogue(funcName);
    }

    private void prologue() {
        int frameSize = 8 + 4 * localVarMap.size();
        sb.append("\taddi sp, sp, ").append(-frameSize).append("\n");
        sb.append("\tsw ra, ").append(frameSize-4).append("(sp)").append("\n");
        sb.append("\tsw fp, ").append(frameSize-8).append("(sp)").append("\n");
        sb.append("\taddi fp, sp, ").append(frameSize).append("\n");

    }

    private void epilogue(String funcName) {
        int frameSize = 8 + 4 * localVarMap.size();
        sb.append(funcName).append("_epilogue:\n");
        sb.append("\tlw a0, 0(sp)\n");
        sb.append("\taddi sp, sp, 4\n");
        sb.append("\tlw fp, ").append(frameSize-8).append("(sp)").append("\n");
        sb.append("\tlw ra, ").append(frameSize-4).append("(sp)").append("\n");
        sb.append("\taddi sp, sp, ").append(frameSize).append("\n");
        sb.append("\tjr ra\n");

    }

    /**
     * push register to stack
     *
     * @param reg register need to push
     */
    private void push(String reg) {
        sb.append("\taddi sp,sp,-4\n");
        sb.append("\tsw ").append(reg).append(", 0(sp)\n");
    }

    /**
     * pop stack value to register
     *
     * @param reg register pop to
     */
    private void pop(String reg) {
        sb.append("\tlw ").append(reg).append(", 0(sp)\n");
        sb.append("\taddi sp,sp,4\n");
    }

    private static class LocalVar {
        private final String name;
        private final String type;
        private final int index;
        private final int line;
        private final int col;

        public LocalVar(String name, int index, int line, int col) {
            this.name = name;
            this.index = index;
            this.line = line;
            this.col = col;
            type = "int";
        }
    }
}
