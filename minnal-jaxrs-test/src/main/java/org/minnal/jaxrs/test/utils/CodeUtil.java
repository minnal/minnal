package org.minnal.jaxrs.test.utils;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.minnal.core.MinnalException;

import java.util.HashMap;
import java.util.Map;

public class CodeUtil {

    private static final Map<String, String> formatterOptions = new HashMap<>();

    static {
        formatterOptions.put(JavaCore.COMPILER_SOURCE, "1.5");
        formatterOptions.put(JavaCore.COMPILER_COMPLIANCE, "1.5");
        formatterOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, "1.5");
    }

    public static String format(String code) {
        return format(code, formatterOptions);
    }

    public static String format(String code, Map<String, String> options) {
        DefaultCodeFormatterOptions cfOptions = DefaultCodeFormatterOptions.getJavaConventionsSettings();
        cfOptions.tab_char = DefaultCodeFormatterOptions.TAB;
        CodeFormatter cf = new DefaultCodeFormatter(cfOptions, options);
        TextEdit te = cf.format(CodeFormatter.K_UNKNOWN, code, 0, code.length(), 0, null);
        IDocument dc = new Document(code);

        try {
            te.apply(dc);
        } catch (Exception e) {
            throw new MinnalException("Failed while formatting the code", e);
        }
        return dc.get();
    }
}