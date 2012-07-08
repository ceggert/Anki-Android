package com.ichi2.libanki.hooks;

import android.util.Log;

import com.ichi2.anki.AnkiDroidApp;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChessFilter extends Hook {

    private static final Pattern fFenPattern = Pattern.compile("\\[fen ?([^\\]]*)\\]([^\\[]+)\\[/fen\\]");
    private static final Pattern fFenOrientationPattern = Pattern.compile("orientation *= *\"?(black|white)\"?");
    private static final String fRenderFen =
    		"(function (fentxt, showBlack) {" +
    		"    fentxt=fentxt.replace(/ .*/g,'');" +
    		"    if (showBlack) {" +
    		"        fentxt = fentxt.split(\"\").reverse().join(\"\");" +
    		"    }" +
    		"    fentxt=fentxt.replace(/r/g,'x');" +
    		"    fentxt=fentxt.replace(/\\\\//g,'</tr><tr>');" +
    		"    fentxt=fentxt.replace(/1/g,'<td></td>');" +
    		"    fentxt=fentxt.replace(/2/g,'<td></td><td></td>');" +
    		"    fentxt=fentxt.replace(/3/g,'<td></td><td></td><td></td>');" +
    		"    fentxt=fentxt.replace(/4/g,'<td></td><td></td><td></td><td></td>');" +
    		"    fentxt=fentxt.replace(/5/g,'<td></td><td></td><td></td><td></td><td></td>');" +
    		"    fentxt=fentxt.replace(/6/g,'<td></td><td></td><td></td><td></td><td></td><td></td>');" +
    		"    fentxt=fentxt.replace(/7/g,'<td></td><td></td><td></td><td></td><td></td><td></td><td></td>');" +
    		"    fentxt=fentxt.replace(/8/g,'<td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>');" +
    		"    fentxt=fentxt.replace(/K/g,'<td>&#9812;</td>');" +
    		"    fentxt=fentxt.replace(/Q/g,'<td>&#9813;</td>');" +
    		"    fentxt=fentxt.replace(/R/g,'<td>&#9814;</td>');" +
    		"    fentxt=fentxt.replace(/B/g,'<td>&#9815;</td>');" +
    		"    fentxt=fentxt.replace(/N/g,'<td>&#9816;</td>');" +
    		"    fentxt=fentxt.replace(/P/g,'<td>&#9817;</td>');" +
    		"    fentxt=fentxt.replace(/k/g,'<td>&#9818;</td>');" +
    		"    fentxt=fentxt.replace(/q/g,'<td>&#9819;</td>');" +
    		"    fentxt=fentxt.replace(/x/g,'<td>&#9820;</td>');" +
    		"    fentxt=fentxt.replace(/b/g,'<td>&#9821;</td>');" +
    		"    fentxt=fentxt.replace(/n/g,'<td>&#9822;</td>');" +
    		"    fentxt=fentxt.replace(/p/g,'<td>&#9823;</td>');" +
    		"    return '<div align=\"center\" width=\"100%%\"><table class=\"chess_board\" cellspacing=\"0\" cellpadding=\"0\"><tr>'+fentxt+'</tr></table></div>';" +
    		"})('%s', %b)";
    @Override
    public Object runFilter(Object arg, Object... args) {
        return fenToChessboard((String) arg);
    }
    public static void install(Hooks h) {
        h.addHook("mungeQA", new ChessFilter());
    }
    public static void uninstall(Hooks h) {
        h.remHook("mungeQA", new ChessFilter());
    }

    private String fenToChessboard(String text) {
        Boolean showBlack = false;
        Matcher mf = fFenPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (mf.find()) {
            if (mf.group(1) != null) {
                Matcher mo = fFenOrientationPattern.matcher(mf.group(1));
                if (mo.find() && mo.group(1) != null && mo.group(1).equalsIgnoreCase("black")) {
                    showBlack = true;
                }
            }
            
            try {
                mf.appendReplacement(sb, "<script type=\"text/javascript\">document.write(" +
                        String.format(Locale.US, fRenderFen, mf.group(2), showBlack) + ");</script>");
            } catch (Throwable e) {
                Log.e(AnkiDroidApp.TAG, "Chess boom!", e);
            }
        }
        mf.appendTail(sb);
        return sb.toString();
    }
}
