package pro.mbroker.app.util;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PageNumeratorHandler implements IEventHandler {
    protected PdfFont font;
    protected float fontSize;
    protected float rightOffset;
    protected float bottomOffset;

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        int pageNumber = pdfDoc.getPageNumber(page);
        String pageNumberString = "Страница " + pageNumber;
        float textWidth = font.getWidth(pageNumberString, fontSize);
        float xRightAligned = page.getPageSize().getWidth() - textWidth - rightOffset;
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdfDoc);
        pdfCanvas.beginText()
                .setFontAndSize(font, fontSize)
                .moveText(xRightAligned, bottomOffset)
                .showText("Страница " + pageNumber)
                .endText();
    }
}




