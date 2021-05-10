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
        sb.append("\t.text\r\n\t.globl\tmain\r\n");
        return super.visitProg(ctx);
    }

    @Override
    public Object visitFunc(MiniDecafParser.FuncContext ctx) {
        String text = ctx.ident.getText();
        if (!"main".equals(text)) {
            throw new RuntimeException("function name is not main.");
        }
        sb.append(text).append(":\r\n");
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
                    lVal = temp.pop();
                    temp.push(-lVal);
                    break;
                case "not":
                    lVal = temp.pop();
                    temp.push(~lVal);
                    break;
                case "lnot":
                    lVal = temp.pop();
                    lVal = lVal == 0 ? 1 : 0;
                    temp.push(lVal);
                    break;
                case "add":
                    rVal = temp.pop();
                    lVal = temp.pop();
                    temp.push(lVal + rVal);
                    break;
                case "sub":
                    rVal = temp.pop();
                    lVal = temp.pop();
                    temp.push(lVal - rVal);
                    break;
                case "mul":
                    rVal = temp.pop();
                    lVal = temp.pop();
                    temp.push(lVal * rVal);
                    break;
                case "dev":
                    rVal = temp.pop();
                    lVal = temp.pop();
                    temp.push(lVal / rVal);
                    break;
                case "rem":
                    rVal = temp.pop();
                    lVal = temp.pop();
                    temp.push(lVal % rVal);
                    break;
                case "push" :
                    String val = split[1];
                    temp.push(Integer.valueOf(val));
                    break;
                case "ret":
                    result = temp.pop();
                    break;
            }
        }
        sb.append("\tli\ta0,").append(result).append("\r\n\tret");
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
            opList.add("push " + ctx.getText());
        }
        return null;
    }
}
