package vv.spoon.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourceCodeFragment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.reference.CtExecutableReference;


public class LogProcessor extends AbstractProcessor<CtInvocation>  {


    @Override
    public boolean isToBeProcessed(CtInvocation candidate) {
        try {
            Class type = candidate.getTarget().getType().getActualClass();
            CtExecutableReference executable = candidate.getExecutable();

            if(type.equals(java.io.PrintStream.class)
                    && isPrint(executable)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void process(CtInvocation element) {
        Object argument = element.getArguments().get(0);

        String snippet = "vv.spoon.logger.LogWriter.out(" + argument
                + "," + isError(element.getTarget()) + ")";

        CtCodeSnippetStatement snippetStmt = getFactory().Code().createCodeSnippetStatement(snippet);
        element.replace(snippetStmt);
    }

    //check in the output stream is error stream or out stream
    protected boolean isError(CtExpression target) {
        return target.toString().endsWith("err");
    }


    //check if executable is java.io.PrintStream.println(...) or java.io.PrintStream.print(...) method
    protected boolean isPrint(CtExecutableReference executable) {
        String toString = executable.toString();
        return toString.startsWith("java.io.PrintStream.println(")
                || toString.startsWith("java.io.PrintStream.print(");
    }

}
