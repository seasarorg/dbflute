package org.seasar.dbflute.velocity;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.seasar.framework.util.FileUtil;

/**
 * @author modified by taktos
 * @author modified by jflute
 */
public class DfGenerator {

    private static final Log _log = LogFactory.getLog(DfGenerator.class);

    /**
     * Where the texen output will placed.
     */
    public static final String OUTPUT_PATH = "output.path";

    /**
     * Where the velocity templates live.
     */
    public static final String TEMPLATE_PATH = "template.path";

    /**
     * Default properties used by texen.
     */
    private Properties props = new Properties();

    /**
     * Context used for generating the texen output.
     */
    private Context controlContext;

    /**
     * Keep track of the file writers used for outputting
     * to files. If we come across a file writer more
     * then once then the additional output will be
     * appended to the file instead of overwritting
     * the contents.
     */
    private Hashtable writers = new Hashtable();

    /**
     * The generator tools used for creating additional
     * output withing the control template. This could
     * use some cleaning up.
     */
    private static DfGenerator instance = new DfGenerator();

    /**
     * This is the encoding for the output file(s).
     */
    protected String outputEncoding;

    /**
     * This is the encoding for the input file(s)
     * (templates).
     */
    protected String inputEncoding;

    /**
     * Default constructor.
     */
    private DfGenerator() {
        setDefaultProps();
    }

    /**
     * Create a new generator object with default properties.
     *
     * @return Generator generator used in the control context.
     */
    public static DfGenerator getInstance() {
        return instance;
    }

    /**
     * Create a new generator object with properties loaded from
     * a file.  If the file does not exist or any other exception
     * occurs during the reading operation the default properties
     * are used.
     *
     * @param String properties used to help populate the control context.
     * @return Generator generator used in the control context.
     */
    public DfGenerator(String propFile) {
        try {
            BufferedInputStream bi = null;
            try {
                bi = new BufferedInputStream(new FileInputStream(propFile));
                props.load(bi);
            } finally {
                if (bi != null) {
                    bi.close();
                }
            }
        } catch (Exception e) {
            /*
             * If something goes wrong we use default properties
             */
            setDefaultProps();
        }
    }

    /**
     * Create a new Generator object with a given property
     * set. The property set will be duplicated.
     *
     * @param Properties properties object to help populate the control context.
     */
    public DfGenerator(Properties props) {
        this.props = (Properties) props.clone();
    }

    /**
     * Set default properties.
     */
    protected void setDefaultProps() {
        props.put("path.output", "output");
        props.put("context.objects.strings", "org.apache.velocity.util.StringUtils");
        props.put("context.objects.files", "org.apache.velocity.texen.util.FileUtil");
        props.put("context.objects.properties", "org.apache.velocity.texen.util.PropertiesUtil");
    }

    /**
     * Set the template path, where Texen will look
     * for Velocity templates.
     *
     * @param String template path for velocity templates.
     */
    public void setTemplatePath(String templatePath) {
        props.put(TEMPLATE_PATH, templatePath);
    }

    /**
     * Get the template path.
     *
     * @return String template path for velocity templates.
     */
    public String getTemplatePath() {
        return props.getProperty(TEMPLATE_PATH);
    }

    /**
     * Set the output path for the generated
     * output.
     *
     * @return String output path for texen output.
     */
    public void setOutputPath(String outputPath) {
        props.put(OUTPUT_PATH, outputPath);
    }

    /**
     * Get the output path for the generated
     * output.
     *
     * @return String output path for texen output.
     */
    public String getOutputPath() {
        return props.getProperty(OUTPUT_PATH);
    }

    /**
     * Set the output encoding.
     */
    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    /**
     * Set the input (template) encoding.
     */
    public void setInputEncoding(String inputEncoding) {
        this.inputEncoding = inputEncoding;
    }

    /**
     * Returns a writer, based on encoding and path.
     *
     * @param path      path to the output file
     * @param encoding  output encoding
     */
    public Writer getWriter(String path, String encoding) throws Exception {
        Writer writer;
        if (encoding == null || encoding.length() == 0 || encoding.equals("8859-1") || encoding.equals("8859_1")) {
            writer = new FileWriter(path);
        } else {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), encoding));
        }
        return writer;
    }

    /**
     * Returns a template, based on encoding and path.
     *
     * @param templateName  name of the template
     * @param encoding      template encoding
     */
    public Template getTemplate(String templateName, String encoding) throws Exception {
        Template template;
        if (encoding == null || encoding.length() == 0 || encoding.equals("8859-1") || encoding.equals("8859_1")) {
            template = Velocity.getTemplate(templateName);
        } else {
            template = Velocity.getTemplate(templateName, encoding);
        }
        return template;
    }

    /**
     * Parse an input and write the output to an output file.  If the
     * output file parameter is null or an empty string the result is
     * returned as a string object.  Otherwise an empty string is returned.
     *
     * @param String input template
     * @param String output file
     */
    public String parse(String inputTemplate, String outputFile) throws Exception {
        return parse(inputTemplate, outputFile, null, null);
    }

    /**
     * Parse an input and write the output to an output file.  If the
     * output file parameter is null or an empty string the result is
     * returned as a string object.  Otherwise an empty string is returned.
     * You can add objects to the context with the objs Hashtable.
     *
     * @param String input template
     * @param String output file
     * @param String id for object to be placed in the control context
     * @param String object to be placed in the context
     * @return String generated output from velocity
     */
    public String parse(String inputTemplate, String outputFile, String objectID, Object object) throws Exception {
        return parse(inputTemplate, null, outputFile, null, objectID, object);
    }

    /**
     * Parse an input and write the output to an output file.  If the
     * output file parameter is null or an empty string the result is
     * returned as a string object.  Otherwise an empty string is returned.
     * You can add objects to the context with the objs Hashtable.
     *
     * @param String input template
     * @param String inputEncoding template encoding
     * @param String output file
     * @param String outputEncoding encoding of output file
     * @param String id for object to be placed in the control context
     * @param String object to be placed in the context
     * @return String generated output from velocity
     */
    public String parse(String inputTemplate, String intputEncoding, String outputFile, String outputEncoding,
            String objectID, Object object) throws Exception {
        if (objectID != null && object != null) {
            controlContext.put(objectID, object);
        }

        Template template = getTemplate(inputTemplate, inputEncoding != null ? inputEncoding : this.inputEncoding);

        if (outputFile == null || outputFile.equals("")) {
            StringWriter sw = new StringWriter();
            template.merge(controlContext, sw);
            return sw.toString();
        } else {
            // /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *  
            // [Extension]
            // If the old file is same as the new file, it does not override it.
            // * * * * * * * * * */
            final File oldFile = new File(getOutputPath() + "/" + outputFile);
            if (oldFile.exists()) {
                StringWriter sw = new StringWriter();
                template.merge(controlContext, sw);
                String newContent = sw.toString();
                String oldContent = new String(FileUtil.getBytes(oldFile), outputEncoding);
                if (newContent.equals(oldContent)) {
                    _log.warn("File not changed. skip to generate!");
                    return "";
                }
                //                final String[] newLines = newContent.split("\\n");
                //                final String[] oldLines = oldContent.split("\\n");
                //                for (int i = 0; i < newLines.length; i++) {
                //                    if (oldLines.length <= i) {
                //                        _log.info("line " + (i + 1) + ":");
                //                        _log.info("pre> ");
                //                        _log.info("new> " + newLines[i]);
                //                        break;
                //                    }
                //                    if (!newLines[i].equals(oldLines[i])) {
                //                        _log.info("line " + i + ":");
                //                        _log.info("pre>");
                //                        _log.info(oldLines[i - 1]);
                //                        _log.info(oldLines[i]);
                //                        if (i + 1 < oldLines.length) {
                //                            _log.info(oldLines[i + 1]);
                //                        }
                //                        _log.info("new>");
                //                        _log.info(newLines[i - 1]);
                //                        _log.info(newLines[i]);
                //                        if (i + 1 < newLines.length) {
                //                            _log.info(newLines[i + 1]);
                //                        }
                //                        break;
                //                    }
                //                }
            }
            Writer writer = null;

            if (writers.get(outputFile) == null) {
                /*
                 * We have never seen this file before so create
                 * a new file writer for it.
                 */
                writer = getWriter(getOutputPath() + File.separator + outputFile,
                        outputEncoding != null ? outputEncoding : this.outputEncoding);

                /*
                 * Place the file writer in our collection
                 * of file writers.
                 */
                writers.put(outputFile, writer);
            } else {
                writer = (Writer) writers.get(outputFile);
            }

            VelocityContext vc = new VelocityContext(controlContext);
            template.merge(vc, writer);

            // commented because it is closed in shutdown();
            //fw.close();

            return "";
        }
    }

    /**
     * Parse the control template and merge it with the control
     * context. This is the starting point in texen.
     *
     * @param String control template
     * @param Context control context
     * @return String generated output
     */
    public String parse(String controlTemplate, Context controlContext) throws Exception {
        this.controlContext = controlContext;
        fillContextDefaults(this.controlContext);
        fillContextProperties(this.controlContext);

        Template template = getTemplate(controlTemplate, inputEncoding);
        StringWriter sw = new StringWriter();
        template.merge(controlContext, sw);

        return sw.toString();
    }

    /**
     * Create a new context and fill it with the elements of the
     * objs Hashtable.  Default objects and objects that comes from
     * the properties of this Generator object is also added.
     *
     * @param Hashtable objects to place in the control context
     * @return Context context filled with objects
     */
    protected Context getContext(Hashtable objs) {
        fillContextHash(controlContext, objs);
        return controlContext;
    }

    /**
     * Add all the contents of a Hashtable to the context.
     *
     * @param Context context to fill with objects
     * @param Hashtable source of objects
     */
    protected void fillContextHash(Context context, Hashtable objs) {
        Enumeration enu = objs.keys();
        while (enu.hasMoreElements()) {
            String key = enu.nextElement().toString();
            context.put(key, objs.get(key));
        }
    }

    /**
     * Add properties that will aways be in the context by default
     *
     * @param Context control context to fill with default values.
     */
    protected void fillContextDefaults(Context context) {
        context.put("generator", instance);
        context.put("outputDirectory", getOutputPath());
    }

    /**
     * Add objects to the context from the current properties.
     *
     * @param Context control context to fill with objects
     *                that are specified in the default.properties
     *                file
     */
    protected void fillContextProperties(Context context) {
        Enumeration enu = props.propertyNames();

        while (enu.hasMoreElements()) {
            String nm = (String) enu.nextElement();
            if (nm.startsWith("context.objects.")) {

                String contextObj = props.getProperty(nm);
                int colon = nm.lastIndexOf('.');
                String contextName = nm.substring(colon + 1);

                try {
                    Class cls = Class.forName(contextObj);
                    Object o = cls.newInstance();
                    context.put(contextName, o);
                } catch (Exception e) {
                    e.printStackTrace();
                    //TO DO: Log Something Here
                }
            }
        }
    }

    /**
     * Properly shut down the generator, right now
     * this is simply flushing and closing the file
     * writers that we have been holding on to.
     */
    public void shutdown() {
        Iterator iterator = writers.values().iterator();

        while (iterator.hasNext()) {
            Writer writer = (Writer) iterator.next();

            try {
                writer.flush();
                writer.close();
            } catch (Exception e) {
                /* do nothing */
            }
        }
        // clear the file writers cache
        writers.clear();
    }
}
