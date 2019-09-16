package org.michael.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019-09-16 11:34
 * Author : Michael.
 */
public class TextPrintFormatUtil {

    public static void main(String[] args) {
        List<String> heads = new ArrayList<>();
        heads.add("name");
        heads.add("age");
        heads.add("gender");

        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add("jack");
        row.add(null);
        row.add("m");
        rows.add(row);

        String t = makeText(heads, rows, "\n");
        System.out.println(t);
    }

    public static String makeText(List<String> heads, List<List<String>> rows, String newLine) {
        List<Integer> maxWidth = new ArrayList<>(heads.size());

        for (String s : heads) {
            int width = s == null ? 4 : s.length();
            maxWidth.add(width);
        }

        for (int r = 0; r < rows.size(); r++) {
            List<String> row = rows.get(r);
            for (int c = 0; c < row.size(); c++) {
                String str = row.get(c);
                int len = str == null ? 4 : str.length();
                Integer old = maxWidth.get(c);
                len = len > old ? len : old;
                maxWidth.set(c, len);
            }
        }

        return makeText0(heads, rows, maxWidth, newLine);
    }

    private static String makeText0(List<String> heads, List<List<String>> rows, List<Integer> maxWidth, String newLine) {
        StringBuilder builder = new StringBuilder();
        String header = makeRow(heads, maxWidth);
        builder.append(header);
        builder.append(newLine);
        builder.append(makeEmptySpace(header.length(), "-"));
        builder.append(newLine);

        for (List<String> row : rows) {
            String tmp = makeRow(row, maxWidth);
            builder.append(tmp);
            builder.append(newLine);
            builder.append(makeEmptySpace(tmp.length(), "-"));
            builder.append(newLine);
        }

        return String.format("%s%s%s", makeEmptySpace(header.length(), "-"), newLine, builder.toString());
    }

    private static String makeRow(List<String> row, List<Integer> maxWidth) {
        StringBuilder builder = new StringBuilder();
        if (row.size() > 0) {
            builder.append("|");
        }
        for (int i = 0; i < row.size(); i++) {
            String c = makeCell(row.get(i), maxWidth.get(i));
            builder.append(c);
        }
        return builder.toString();
    }

    private static String makeCell(String str, int maxWidth) {
        int strlen = str == null ? 4 : str.length();
        int rightSpace = maxWidth - strlen;
        String rightSpaceStr = makeEmptySpace(rightSpace, " ");

        String r = String.format(" %s%s |", str, rightSpaceStr);
        return r;
    }

    private static String makeEmptySpace(int n, String c) {
        StringBuilder builder = new StringBuilder(8);
        for (int i = 0; i < n; i++) {
            builder.append(c);
        }
        return builder.toString();
    }

}
