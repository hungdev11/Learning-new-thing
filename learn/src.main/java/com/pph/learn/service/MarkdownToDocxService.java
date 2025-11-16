import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class MarkdownToDocxService {

    public byte[] markdownToDocx(String markdown) throws Exception {
        // Convert Markdown to HTML first.
        MutableDataSet options = new MutableDataSet();
        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
        String html = renderer.render(parser.parse(markdown));

        // Use Jsoup to parse the HTML and Apache POI to build the DOCX.
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            Document htmlDoc = Jsoup.parse(html);
            Elements elements = htmlDoc.body().children();

            for (Element element : elements) {
                if (element.tagName().equals("h1")) {
                    XWPFParagraph p = doc.createParagraph();
                    p.setStyle("Heading1");
                    XWPFRun run = p.createRun();
                    run.setBold(true);
                    run.setText(element.text());
                } else if (element.tagName().equals("h2")) {
                    XWPFParagraph p = doc.createParagraph();
                    p.setStyle("Heading2");
                    XWPFRun run = p.createRun();
                    run.setBold(true);
                    run.setText(element.text());
                } else if (element.tagName().equals("p")) {
                    XWPFParagraph p = doc.createParagraph();
                    XWPFRun run = p.createRun();
                    run.setText(element.text());
                } else if (element.tagName().equals("ul")) {
                    for (Element li : element.select("li")) {
                        XWPFParagraph p = doc.createParagraph();
                        XWPFRun run = p.createRun();
                        run.setText("• " + li.text());
                    }
                } else {
                    XWPFParagraph p = doc.createParagraph();
                    XWPFRun run = p.createRun();
                    run.setText(element.text());
                }
            }

            doc.write(os);
            return os.toByteArray();
        }
    }
}