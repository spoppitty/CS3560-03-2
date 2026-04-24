package report;

import java.io.IOException;
import java.util.List;
import model.InventoryItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
// import service.InventoryService;
// import model.InventoryItem;

public class generateLowStockPDF {

    public static void generateLowStockReport(List<InventoryItem> items, String filePath) throws IOException {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(document, page);

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 14);
        content.newLineAtOffset(50, 750);
        content.showText("Low Stock Inventory Report");
        content.endText();

        int y = 700;

        content.setFont(PDType1Font.HELVETICA, 10);

        for (InventoryItem item : items) {
            String line = item.getInventoryID()
                    + " | " + item.getProductItem().getProductName()
                    + " | Qty: " + item.getQuantityOnHand()
                    + " | Reorder: " + item.getReorderLevel();

            content.beginText();
            content.newLineAtOffset(50, y);
            content.showText(line);
            content.endText();

            y -= 20;

            if (y < 50) break; // simple page limit handling
        }

        content.close();
        document.save(filePath);
        document.close();
    }
}