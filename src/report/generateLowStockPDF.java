package report;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import model.InventoryItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Generates a PDF for inventory items that are marked as low in stock.
 */
public class generateLowStockPDF {

    /**
     * Layout constraints.
     */
    private static final float MARGIN = 50f;
    private static final float TITLE_Y = 760f;
    private static final float SUBTITLE_Y = 740f;
    private static final float TABLE_TOP_Y = 700f;
    private static final float ROW_HEIGHT = 22f;
    private static final float BOTTOM_MARGIN = 60f;
    private static final float[] COLUMN_WIDTHS = {90f, 220f, 70f, 90f};
    private static final String[] HEADERS = {"Inventory ID", "Product", "Quantity", "Reorder Level"};

    /**
     * Creates a low stock PDF report with a title, date, and table of items.
     *
     * @param items low stock invenotyr items to include
     * @param filePath destination file path for generated PDF
     * @throws IOException when PDF cannot be written
     */
    public static void generateLowStockReport(List<InventoryItem> items, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            drawTitle(content, items.size());
            drawTableHeader(content, TABLE_TOP_Y);

            float y = TABLE_TOP_Y - ROW_HEIGHT;

            for (InventoryItem item : items) {
                if (y < BOTTOM_MARGIN) {
                    content.close();
                    page = new PDPage(PDRectangle.LETTER);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    drawTitle(content, items.size());
                    drawTableHeader(content, TABLE_TOP_Y);
                    y = TABLE_TOP_Y - ROW_HEIGHT;
                }

                drawRow(content, y, item);
                y -= ROW_HEIGHT;
            }

            if (items.isEmpty()) {
                writeText(content, MARGIN, TABLE_TOP_Y - ROW_HEIGHT, PDType1Font.HELVETICA_OBLIQUE, 11,
                        "No low stock items were found.");
            }

            content.close();
            document.save(filePath);
        }
    }

    /**
     * Draws the report title and summary information at the top of page.
     */
    private static void drawTitle(PDPageContentStream content, int itemCount) throws IOException {
        writeText(content, MARGIN, TITLE_Y, PDType1Font.HELVETICA_BOLD, 18, "Low Stock Inventory Report");
        writeText(content, MARGIN, SUBTITLE_Y, PDType1Font.HELVETICA, 11,
                "Generated " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
                        + "  |  Low stock items: " + itemCount);
    }

    /**
     * Draws the table header.
     * 
     */
    private static void drawTableHeader(PDPageContentStream content, float y) throws IOException {
        float x = MARGIN;

        for (int i = 0; i < HEADERS.length; i++) {
            drawCell(content, x, y, COLUMN_WIDTHS[i], ROW_HEIGHT, HEADERS[i], true);
            x += COLUMN_WIDTHS[i];
        }
    }

    /**
     * Draws one ivnentory row in the low stock table.
     */
    private static void drawRow(PDPageContentStream content, float y, InventoryItem item) throws IOException {
        float x = MARGIN;
        String[] values = {
                safeText(item.getInventoryID(), 14),
                safeText(item.getProductItem().getProductName(), 34),
                String.valueOf(item.getQuantityOnHand()),
                String.valueOf(item.getReorderLevel())
        };

        for (int i = 0; i < values.length; i++) {
            drawCell(content, x, y, COLUMN_WIDTHS[i], ROW_HEIGHT, values[i], false);
            x += COLUMN_WIDTHS[i];
        }
    }
    /**
    * Draws one table cell border and its text value.
    */
    private static void drawCell(PDPageContentStream content, float x, float y, float width, float height,
                                 String text, boolean header) throws IOException {
        content.addRect(x, y, width, height);
        content.stroke();

        writeText(content, x + 6, y + 7,
                header ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA,
                10,
                text);
    }

    /**
    * Writes a single line of text at given page position.
    */
    private static void writeText(PDPageContentStream content, float x, float y,
                                  PDType1Font font, int fontSize, String text) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    /**
     * Shortens long text so it fits inside a table cell.
     */
    private static String safeText(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
