package minidecaf;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileWriter;

/**
 * 这是一个 MiniDecaf 的编译器，简便起见我们只做了单遍遍历。
 * 
 * @author  Namasikanam
 * @since    2020-09-11
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2
            || args[0].equals("-h")
            || args[0].equals("--help")) {
            System.out.print("Usage: minidecaf <input minidecaf file> <output riscv assembly file>\n");
            return;
        }

        /* input file --- lexer ---> tokens
                                            --- parser ---> tree
                                                                 --- visitor ---> riscv assembly
                                                                                                 --- writer ---> output file */
        CharStream input = CharStreams.fromFileName(args[0]);
        String oriStr = String.valueOf(input);
        MiniDecafLexer lexer = new MiniDecafLexer(input);
        DefaultErrorListener errorListener = new DefaultErrorListener();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        MiniDecafParser parser = new MiniDecafParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        ParseTree tree = parser.prog();
        MainVisitor visitor = new MainVisitor(oriStr);
        visitor.visit(tree);
        
        FileWriter writer = new FileWriter(args[1]);
        writer.write(visitor.getResult());
        writer.close();
    }
}
