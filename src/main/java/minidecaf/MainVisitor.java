package minidecaf;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class MainVisitor extends MiniDecafBaseVisitor<Object> {

    private final StringBuilder sb;
    private final List<String> opList;
//    private final Stack<String> opStack;

    public MainVisitor(StringBuilder sb) {
        this.sb = sb;
        opList = new ArrayList<>();
    }

    @Override
    public Object visitProg(MiniDecafParser.ProgContext ctx) {
        sb.append("\t.text\n\t.globl\tmain\n");
        return super.visitProg(ctx);
    }

    @Override
    public Object visitFunc(MiniDecafParser.FuncContext ctx) {
        String text = ctx.ident.getText();
        if (!"main".equals(text)) {
            throw new RuntimeException("function name is not main.");
        }
        sb.append(text).append(":\n");
        return super.visitFunc(ctx);
    }

    @Override
    public Object visitStat(MiniDecafParser.StatContext ctx) {
        visit(ctx.expr());
        opList.add("ret");
        transIR();
        return null;
    }

    private void transIR() {
        int rVal;
        int lVal;
        Integer result = 0;
        Stack<Integer> temp = new Stack<>();
        for (String opStr : opList) {
            String[] split = opStr.split(" ");
            String op = split[0];
            switch (op) {
                case "neg":
                    pop("t0");
                    sb.append("\tneg t0,t0\n");
                    push("t0");
                    break;
                case "not":
                    pop("t0");
                    sb.append("\tnot t0,t0\n");
                    push("t0");
                    break;
                case "lnot":
                    pop("t0");
                    sb.append("\tseqz t0,t0\n");
                    push("t0");
                    break;
                case "add":
                    pop("t1"); // rvalue
                    pop("t0"); // lvalue
                    sb.append("\tadd t0,t0,t1\n");
                    push("t0");
                    break;
                case "sub":
                    pop("t1"); // rvalue
                    pop("t0"); // lvalue
                    sb.append("\tsub t0,t0,t1\n");
                    push("t0");
                    break;
                case "mul":
                    pop("t1"); // rvalue
                    pop("t0"); // lvalue
                    sb.append("\tmul t0,t0,t1\n");
                    push("t0");
                    break;
                case "div":
                    pop("t1"); // rvalue
                    pop("t0"); // lvalue
                    sb.append("\tdiv t0,t0,t1\n");
                    push("t0");
                    break;
                case "rem":
                    pop("t1"); // rvalue
                    pop("t0"); // lvalue
                    sb.append("\trem t0,t0,t1\n");
                    push("t0");
                    break;
                case "push":
                    String val = split[1];
                    sb.append("\tli t0," + val + "\n");
                    push("t0");
                    break;
                case "ret":
                    pop("a0");
                    sb.append("\tret\n");
                    break;
            }
        }
    }

    /**
     * push register to stack
     *
     * @param reg register need to push
     */
    private void push(String reg) {
        sb.append("\taddi sp,sp,4\n")
                .append("\tsw " + reg + ", 0(sp)\n");
    }

    /**
     * pop stack value to register
     * @param reg register pop to
     */
    private void pop(String reg) {
        sb.append("\tlw " + reg + ", 0(sp)\n")
                .append("\taddi sp,sp,-4\n");
    }

    @Override
    public Object visitExpr(MiniDecafParser.ExprContext ctx) {
        super.visitExpr(ctx);
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
            opList.add(opText);
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
            opList.add(opText);
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
            opList.add(opText);
        } else {
            visit(ctx.primary());
        }
        return null;
    }

    @Override
    public Object visitPrimary(MiniDecafParser.PrimaryContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.expr());
        } else {
            String text = ctx.getText();
            opList.add("push " + Integer.parseInt(text));
        }
        return null;
    }
}
