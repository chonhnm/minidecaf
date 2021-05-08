package minidecaf;

public final class MainVisitor extends MiniDecafBaseVisitor<Object> {

    private final String oriStr;
    private String resultStr;

    public MainVisitor(String input) {
        this.oriStr = input;
        resultStr = "\t.text\n\t.globl\tmain\nmain:\n\tli\ta0,X\n\tret\n";
    }

    public String getResult() {
        return resultStr;
    }

    public String getOriStr() {
        return oriStr;
    }

    @Override
    public Object visitFuncName(MiniDecafParser.FuncNameContext ctx) {
        String text = ctx.getText();
        if (!"main".equals(text)) {
            throw new RuntimeException("function name is not main.");
        }
        return super.visitFuncName(ctx);
    }

    @Override
    public Object visitExpr(MiniDecafParser.ExprContext ctx) {
        String text = ctx.getText();
        int integer = Integer.parseInt(text);
        if (integer < 0) {
            throw new RuntimeException("do not support negative number.");
        }
        resultStr = resultStr.replace("X", text);
        return super.visitExpr(ctx);
    }
}
