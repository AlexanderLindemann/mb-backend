package pro.mbroker.app.util;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FooterHandler implements IEventHandler {
    private ImageData footerImgData;
    private float fitWidthImage;
    private float fitHeightImage;
    private float leftPositionImage;
    private float bottomPositionImage;

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        Rectangle pageSize = page.getPageSize();

        Image footerImage = new Image(footerImgData).scaleToFit(fitWidthImage, fitHeightImage);

        float x = (pageSize.getLeft() + leftPositionImage);
        float y = pageSize.getBottom() + bottomPositionImage;

        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
        Canvas canvas = new Canvas(pdfCanvas, pageSize);
        canvas.add(footerImage.setHorizontalAlignment(HorizontalAlignment.CENTER).setFixedPosition(x, y));
    }
}




