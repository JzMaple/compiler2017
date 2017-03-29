package com.mercy;

import com.mercy.compiler.AST.AST;
import com.mercy.compiler.FrontEnd.BuildListener;
import com.mercy.compiler.FrontEnd.ParserErrorListener;
import com.mercy.compiler.Parser.MalicLexer;
import com.mercy.compiler.Parser.MalicParser;
import com.mercy.compiler.Utility.SemanticError;
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStream is = new FileInputStream("testcase/test.c");
        try {
            compile(is);
        } catch (SemanticError error) {
            System.err.println(error.getMessage());
            System.exit(1);
        } catch (InternalError error) {
            System.err.println(error.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void compile(InputStream sourceCode) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream(sourceCode);
        MalicLexer lexer = new MalicLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MalicParser parser = new MalicParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ParserErrorListener());
        ParseTree tree = parser.compilationUnit();

        ParseTreeWalker walker = new ParseTreeWalker();
        BuildListener listener = new BuildListener();

        walker.walk(listener, tree);

        AST ast  = listener.getAST();
        ast.resolveSymbol();
        ast.checkType();
    }
}
