package pro.mbroker.app.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.TraversalUtil;
import org.docx4j.wml.P;
import org.docx4j.wml.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor
public final class DocxFieldExtractor extends TraversalUtil.CallbackImpl {
    private final Pattern FIELD_PATTERN = Pattern.compile("«(.*?)»");
    @Getter
    private final Set<String> fields = new HashSet<>();

    private final StringBuilder accumulatedText = new StringBuilder();

    @Override
    public List<Object> apply(Object o) {
        if (o instanceof Text) {
            Text text = (Text) o;
            accumulatedText.append(text.getValue());
        } else if (o instanceof P) {
            processAccumulatedText();
        }
        return null;
    }

    private void processAccumulatedText() {
        Matcher matcher = FIELD_PATTERN.matcher(accumulatedText.toString());
        while (matcher.find()) {
            String fieldName = matcher.group(1).trim();
            if (fieldName.length() > 0) {
                fields.add(fieldName);
                accumulatedText.setLength(0);
            }
        }
    }
}

