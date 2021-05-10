package minidecaf;

import java.util.Stack;

public final class MainVisitor extends MiniDecafBaseVisitor<Object> {

    private final StringBuilder sb;
    private final Stack<Integer> stack;

    public MainVisitor(StringBuilder sb) {
        this.sb = sb;
        stack = new Stack<>();
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
        sb.append("main:\r\n");
        return super.visitFunc(ctx);
    }

    @Override
    public Object visitStat(MiniDecafParser.StatContext ctx) {
        visit(ctx.expr());
        sb.append("\tret");
        return null;
    }

    @Override
    public Object visitExpr(MiniDecafParser.ExprContext ctx) {
        super.visitExpr(ctx);
        Integer val = stack.pop();
        sb.append("\tli\ta0,").append(val).append("\r\n");
        return null;
    }

    @Override
    public Object visitUnary(MiniDecafParser.UnaryContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount > 1) {
            visit(ctx.unary());
            String opText = ctx.op.getText();
            Integer val = stack.pop();
            switch (opText) {
                case "-" :
                    val = -val;
                    break;
                case "!":
                    val = val == 0 ? 1 : 0;
                    break;
                case "~":
                    val = ~val;
                    break;
            }
            stack.push(val);
        } else {
            stack.push(Integer.valueOf(ctx.getText()));
        }
        return null;
    }
}
