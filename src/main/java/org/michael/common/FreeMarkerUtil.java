package org.michael.common;

import freemarker.template.Template;
import org.michael.common.utils.CommonUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * Created on 2019-09-16 11:19
 * Author : Michael.
 */
public class FreeMarkerUtil {

    public static String fillVariables(Map<String, Object> ctx, String template) {
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(template.getBytes(CommonUtil.UTF8)));
        freemarker.template.Configuration conf = new freemarker.template.Configuration();
        try {
            Template t = new Template("FreeMarkerTemplate", reader, conf);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(128);
            OutputStreamWriter writer = new OutputStreamWriter(bos);
            t.process(ctx, writer);
            writer.flush();
            bos.flush();
            byte[] buf = bos.toByteArray();
            return new String(buf, CommonUtil.UTF8);
        } catch (Exception e) {
            throw new RuntimeException("Free marker failed", e);
        }
    }

}
