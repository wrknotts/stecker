package stecker.util.pygments;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.python.core.PyList;
import org.python.util.PythonInterpreter;

import com.google.common.io.Files;

public class CssStyleWriter {

    public static final String STYLE_FILE_EXTENSION = ".css";

    private static final String PGYMENTIZE = "import sys\n" + "import pygments.cmdline\n"
            + "try:\n" + "\tsys.exit(pygments.cmdline.main(args))\n"
            + "except KeyboardInterrupt:\n" + "\tsys.exit(1)";

    private static final String LIST_STYLES = "from pygments.styles import get_all_styles\n"
            + "styles = list(get_all_styles())\n";

    private final String[] args;
    private boolean shouldCreate;
    private final PythonInterpreter python;
    private final File targetDir;

    public CssStyleWriter(String[] args) throws Exception {
        this.args = args;
        this.python = new PythonInterpreter();

        validateArgs();
        targetDir = validateTargetDir(args[0]);
        
        System.out.println(String.format("%s: Writing Pygments styles to \'%s\'", this.getClass().getName(), targetDir.getAbsolutePath()));
    }

    PyList list() {

        try {
            python.exec(LIST_STYLES);
            PyList list = python.get("styles", PyList.class);
            list.sort();
            
            return list;

        }
        catch (Throwable t) {
            throw t;
        }

    }

    String retrieve(String name) throws Exception {

        List<String> args = new ArrayList<String>();
        args.add("pygmentize");
        args.add("-S");
        args.add(name);
        args.add("-f");
        args.add("html");
        args.add("-a");
        args.add(".stecker");

        StringWriter out = new StringWriter();
        python.setOut(out);
        StringWriter err = new StringWriter();
        python.setErr(err);

        python.set("args", args);

        String result = null;
        try {
            python.exec(PGYMENTIZE);

        }
        catch (Throwable t) {
            // consistently getting the following error, not sure why.
            //
            // Traceback (most recent call last):
            // ..File "<string>", line 4, in <module>
            // ..File "<string>", line 4, in <module>
            // SystemExit: 0
            //
            // Ignoring for now, obviously there are better choices
            // System.err.print(t);
        }
        finally {
            result = out.toString();
        }

        if (result.isEmpty()) {
            String error = err.toString();
            if (error.isEmpty()) {
                error = "Unknown Style!";
            }
            throw new IllegalArgumentException(error);
        }

        return result;
    }

    void write(String style) throws Exception {

        String content = retrieve(style);
        
        String fileName = style + STYLE_FILE_EXTENSION;
        
        System.out.println(String.format("%s: Writing style. File name: \'%s\'", this.getClass().getName(), fileName));

        Files.write(content.getBytes(), new File(targetDir, style + STYLE_FILE_EXTENSION));

    }

    void writeAll() throws Exception {

        PyList styles = list();

        System.out.println(String.format("%s: Found \'%d\' styles.", this.getClass().getName(), styles.size()));

        for (Object o : styles) {
            write((String) o);
        }

    }

    private void validateArgs() throws Exception {

        if (args.length == 0) {
            throw new IllegalArgumentException("args" /* + usage() */);
        }

        if ((args[1].equalsIgnoreCase("true") || (args[1].equalsIgnoreCase("false")))) {
            shouldCreate = Boolean.parseBoolean(args[1]);
        }
        else {
            throw new IllegalArgumentException("create" /* + usage() */);
        }

    }

    private File validateTargetDir(String targetDirPath) throws Exception {

        File targetDir = new File(targetDirPath);

        if (!targetDir.exists()) {
            if (shouldCreate) {
                System.out.println(String.format("%s: Target directory does not exist, creating. Path: \'%s\'", this.getClass().getName(), targetDir.getAbsolutePath()));
                Files.createParentDirs(new File(targetDir, "dummy"));
            }
            else {
                throw new IllegalArgumentException("target dir");
            }
        }

        return targetDir;
    }

    // - target directory (required)
    // - true to create target dir if not exist (required)
    // - style name (optional)
    public static void main(String[] args) {

        try {

            CssStyleWriter writer = new CssStyleWriter(args);

            if (args.length == 3) {
                writer.write(args[2]);
            }
            else {
                writer.writeAll();
            }

        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
