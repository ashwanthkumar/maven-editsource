package in.ashwanthkumar.maven.editsource;

import in.ashwanthkumar.utils.collections.Lists;
import in.ashwanthkumar.utils.io.IO;
import in.ashwanthkumar.utils.template.TemplateParser;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mojo(defaultPhase = LifecyclePhase.GENERATE_RESOURCES, name = "edit")
public class Edit extends AbstractMojo {
    @Parameter(required = true)
    private Map<String, Object> variables;

    @Parameter(property = "edit.files")
    private List<Input> files;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Looking for files to be edited in " + Lists.mkString(files));
        for (String name : variables.keySet()) {
            getLog().info("Editing " + name + " as " + variables.get(name));
        }

        for (Input file : files) {
            try {
                File inputFile = new File(file.getInput());
                File outFile = new File(file.getOutput());
                File tmpFile;
                if (outFile.isDirectory()) {
                    outFile.mkdirs();
                    tmpFile = new File(outFile, inputFile.getName() + ".tmp");
                } else {
                    outFile.getParentFile().mkdirs();
                    tmpFile = new File(outFile.getParent(), outFile.getName() + ".tmp");
                }

                listFilesAndRender(inputFile, tmpFile);
                getLog().info("Copying the file " + tmpFile.getAbsolutePath() + " to " + outFile.getAbsolutePath());
                FileUtils.copyFile(tmpFile, outFile);
                getLog().info("Deleting the file " + tmpFile.getAbsolutePath());
                tmpFile.delete();
            } catch (IOException e) {
                getLog().error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    private void listFilesAndRender(File input, File output) throws IOException {
        if (input.isFile()) {
            parseFileAndRender(input, output);
        } else {
            File[] files = input.listFiles();
            if (files != null) {
                for (File file : files) {
                    listFilesAndRender(file, new File(output.getAbsolutePath() + "/" + file.getName()));
                }
            }
        }
    }

    private void parseFileAndRender(File input, File output) throws IOException {
        getLog().info("Opening " + input.getAbsolutePath() + " and writing the rendered version to " + output.getAbsolutePath());
        Iterator<String> lines = IO.linesFromFile(input);
        PrintWriter writer = new PrintWriter(output);
        TemplateParser parser = new TemplateParser(variables);
        while (lines.hasNext()) {
            writer.println(parser.render(lines.next()));
        }
        writer.flush();
        writer.close();
        getLog().info("Closing the file " + output.getAbsolutePath());
    }
}
