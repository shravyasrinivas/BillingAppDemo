package com.example.BillingDemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.sax.Element;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvoiceActivity extends AppCompatActivity {

    private WebView webView;
    private String htmlTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        webView = findViewById(R.id.webView);
        htmlTemplate = getIntent().getStringExtra("htmlTemplate");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadDataWithBaseURL(null, htmlTemplate, "text/html", "UTF-8", null);
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInvoice();
            }
        });

        addSaveButton();
    }

    private void addSaveButton() {
        webView.loadUrl("javascript:(function() { " +
                "   var saveButton = document.createElement('button');" +
                "   saveButton.innerHTML = 'Save';" +
                "   saveButton.onclick = function() { " +
                "       saveInvoice();" +
                "   };" +
                "   document.body.appendChild(saveButton);" +
                "})()");
    }

    public void saveInvoice() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            generateAndSavePdf();
        }
    }
    private void generateAndSavePdf() {
        // Measure WebView content height
        webView.measure(View.MeasureSpec.makeMeasureSpec(webView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int webViewContentHeight = webView.getMeasuredHeight();

        // Extract customer name from HTML template
        String customerName = extractCustomerNameFromHtml(htmlTemplate);

        // Create PDF document and page
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(webView.getWidth(), webViewContentHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        // Create a bitmap with the measured height
        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webViewContentHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE); // Set canvas background color to white
        webView.draw(canvas);

        // Render the bitmap on the PDF page
        page.getCanvas().drawBitmap(bitmap, 0, 0, null);

        // Finish the PDF page and save the document
        document.finishPage(page);

        // Define the directory and file path for the PDF
        String fileName = customerName + "_invoice.pdf";
        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(directoryPath, fileName);

        try {
            // Write the PDF document to the file
            FileOutputStream outputStream = new FileOutputStream(file);
            document.writeTo(outputStream);
            document.close();
            outputStream.close();
            Toast.makeText(this, "Invoice saved as PDF", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save invoice", Toast.LENGTH_SHORT).show();
        }
    }

    private String extractCustomerNameFromHtml(String html) {
        String customerName = "";

        // Regular expression pattern to match the customer name
        Pattern pattern = Pattern.compile("<th>Customer Name</th>\\s*<td>(.*?)</td>");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            customerName = matcher.group(1);
        }

        return customerName;
    }
}

//    private void generateAndSavePdf() {
//        // Measure WebView content height
//        webView.measure(View.MeasureSpec.makeMeasureSpec(webView.getWidth(), View.MeasureSpec.EXACTLY),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        int webViewContentHeight = webView.getMeasuredHeight();
//
//        // Create PDF document and page
//        PdfDocument document = new PdfDocument();
//        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(webView.getWidth(), webViewContentHeight, 1).create();
//        PdfDocument.Page page = document.startPage(pageInfo);
//
//        // Create a bitmap with the measured height
//        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webViewContentHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawColor(Color.WHITE); // Set canvas background color to white
//        webView.draw(canvas);
//
//        // Render the bitmap on the PDF page
//        page.getCanvas().drawBitmap(bitmap, 0, 0, null);
//
//        // Finish the PDF page and save the document
//        document.finishPage(page);
//
//        // Define the directory and file path for the PDF
//        String directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
//        File file = new File(directoryPath, "invoice.pdf");
//
//        try {
//            // Write the PDF document to the file
//            FileOutputStream outputStream = new FileOutputStream(file);
//            document.writeTo(outputStream);
//            document.close();
//            outputStream.close();
//            Toast.makeText(this, "Invoice saved as PDF", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to save invoice", Toast.LENGTH_SHORT).show();
//        }
//    }

