package ma.ifdose.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.languages.ArabicLigaturizer;
import com.itextpdf.text.pdf.languages.LanguageProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ma.ifdose.app.Singleton.HttpSingleton;
import timber.log.Timber;

public class GenerateRapportActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    protected static String fName = "", lName = "", alimentsPDej = "", alimentsDej = "",
            alimentsCol = "", alimentsDin = "";
    protected static float rd, rp, rc, rdi, is, obj;
    protected static float r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16;
    protected static String c1,c2,c3,c4;
    protected static float gluco0, gluco1, gluco2, gluco3;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final String TAG = "GenerateRapportActivity";
    private String email;

    // pour la gestion des permissions sur android 6+
    private SharedPreferences sp;
    private SharedPreferences spGlycemies;

    private String format = "yyyy-MM-dd HH:mm:ss";
    private String format2 = "yyyy-MM-dd";
    private String fileName;
    private String[] a1, a2, a3, a4;


    // generatePDFServer
    private RequestQueue queue;


    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            Toast.makeText(this.getBaseContext(), getString(ma.ifdose.app.R.string.permission_denied),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Permission denied");
        } else {

            Log.i(TAG, getString(ma.ifdose.app.R.string.generateing_pdf));
            printDocumentIText();
            Toast.makeText(this.getBaseContext(), getString(ma.ifdose.app.R.string.generateing_pdf), Toast.LENGTH_SHORT).show();
            sp.edit().putString("fileName", fileName).apply();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "generating pdf ...");
            printDocumentIText();
            Toast.makeText(this.getBaseContext(), getString(ma.ifdose.app.R.string.pdf_printed), Toast.LENGTH_SHORT).show();
            sp.edit().putString("fileName", fileName).apply();

            // permission was granted, yay! Do the
            // contacts-related task you need to do.

        } else {
            Toast.makeText(this.getBaseContext(), getString(ma.ifdose.app.R.string.permission_denied),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Permission denied");

            // permission denied, boo! Disable the
            // functionality that depends on this permission.
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ma.ifdose.app.R.layout.activity_generate_rapport);
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        spGlycemies = getApplicationContext().getSharedPreferences("glycemies", Context.MODE_PRIVATE);
        getSPProperties();

        // pour generatePdfServer
        queue = HttpSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        queue.start();
    }

    public void getSPProperties() {

        fName = sp.getString("nom", "Nom");
        lName = sp.getString("pren", "Prenom");

        rd = sp.getFloat("rd", 0);
        rp = sp.getFloat("rp", 0);
        rc = sp.getFloat("rc", 0);
        rdi = sp.getFloat("rdi", 0);
        is = sp.getFloat("is", 0);
        obj = sp.getFloat("obj", 0);

        r1 = spGlycemies.getFloat("glucoAvantRepas0", 0);
        r2 = spGlycemies.getFloat("unitInject0", 0);
        r3 = spGlycemies.getFloat("GlycAp0", 0);
        r4 = spGlycemies.getFloat("confirmDose0", 0);
        c1 = spGlycemies.getString("comment1","");

        r5 = spGlycemies.getFloat("glucoAvantRepas1", 0);
        r6 = spGlycemies.getFloat("unitInject1", 0);
        r7 = spGlycemies.getFloat("GlycAp1", 0);
        r8 = spGlycemies.getFloat("confirmDose1", 0);
        c2 = spGlycemies.getString("comment2","");

        r9  = spGlycemies.getFloat("glucoAvantRepas2", 0);
        r10 = spGlycemies.getFloat("unitInject2", 0);
        r11 = spGlycemies.getFloat("GlycAp2", 0);
        r12 = spGlycemies.getFloat("confirmDose2", 0);
        c3  = spGlycemies.getString("comment3","");

        r13 = spGlycemies.getFloat("glucoAvantRepas3", 0);
        r14 = spGlycemies.getFloat("unitInject3", 0);
        r15 = spGlycemies.getFloat("GlycAp3", 0);
        r16 = spGlycemies.getFloat("confirmDose3", 0);
        c4  = spGlycemies.getString("comment4","");

        alimentsPDej = spGlycemies.getString("aliments0", "-");
        alimentsDej = spGlycemies.getString("aliments1", "-");
        alimentsCol = spGlycemies.getString("aliments2", "-");
        alimentsDin = spGlycemies.getString("aliments3", "-");

        gluco0 = spGlycemies.getFloat("gluco0", 0);
        gluco1 = spGlycemies.getFloat("gluco1", 0);
        gluco2 = spGlycemies.getFloat("gluco2", 0);
        gluco3 = spGlycemies.getFloat("gluco3", 0);

        a1 = alimentsPDej.split(";");
        a2 = alimentsDej.split(";");
        a3 = alimentsCol.split(";");
        a4 = alimentsDin.split(";");

        email = sp.getString("medecin_email", getString(ma.ifdose.app.R.string.medecin_email));

        Timber.i(alimentsPDej);
        Timber.i(alimentsDej);
        Timber.i(alimentsCol);
        Timber.i(alimentsDin);

    }

    private void resetSPValues() {

        SharedPreferences.Editor edit = spGlycemies.edit();

        edit.putString("aliments0", "");
        edit.putString("aliments1", "");
        edit.putString("aliments2", "");
        edit.putString("aliments3", "");

        edit.remove("gluco0");
        edit.remove("gluco1");
        edit.remove("gluco2");
        edit.remove("gluco3");

        edit.remove("comment4");
        edit.remove("GlycAp3");
        edit.remove("unitInject3");
        edit.remove("glucoAvantRepas3");
        edit.remove("confirmDose3");
        edit.remove("comment3");
        edit.remove("GlycAp2");
        edit.remove("unitInject2");
        edit.remove("glucoAvantRepas2");
        edit.remove("confirmDose2");
        edit.remove("comment2");
        edit.remove("GlycAp1");
        edit.remove("unitInject1");
        edit.remove("glucoAvantRepas1");
        edit.remove("confirmDose1");
        edit.remove("comment1");
        edit.remove("GlycAp0");
        edit.remove("unitInject0");
        edit.remove("glucoAvantRepas0");
        edit.remove("confirmDose0");

        edit.apply();

    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void generatePdf(View v) {
        Log.i(TAG, "generating pdf ...");
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
        String jobName = this.getString(android.R.string.unknownName) + " Document";
        printManager.print(jobName, new PrintedPdfDocumentAdapter(), null);
        Toast.makeText(this.getBaseContext(), "Printed", Toast.LENGTH_SHORT).show();
        sp.edit().putString("fileName", fileName).apply();
    }
    */

    public void generatePdfV2(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            verifyStoragePermissions(this);
        }
        else {
            Timber.i(TAG, "bellow M");
            Timber.i(TAG, "generating pdf ...");
            printDocumentIText();
            Toast.makeText(this.getBaseContext(), "Printed", Toast.LENGTH_SHORT).show();
            sp.edit().putString("fileName", fileName).apply();
        }

    }
    /*
    public void sendEmailOnClick(View v) {
        if (fileName != null) {
            String[] mailto = {email};
            File pdfFolder = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            Uri uri = Uri.fromFile(new File(pdfFolder, fileName));
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, mailto);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Calc PDF Report");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "PDF Report");
            emailIntent.setType("application/pdf");
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Send email using:"));
            // clear fileName after sending the file
            sp.edit().putString("fileName", null).apply();
            fileName = null;
        } else {
            Snackbar.make(v, getString(R.string.pdfSendingErrorMsg),
                    Snackbar.LENGTH_SHORT).show();
//            Toast.makeText(getBaseContext(), getString(R.string.pdfSendingErrorMsg),
//                    Toast.LENGTH_LONG).show();
        }
    }
    */


    public void printDocumentIText() {

        Font bigFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font darkFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC, BaseColor.DARK_GRAY);
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 13, Font.BOLD);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

        BaseFont myBaseFont = null;
        try {
            myBaseFont = BaseFont.createFont("assets/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            Timber.e("" + e.getMessage());
        }
        Font normalFont = new Font(myBaseFont, 12, Font.NORMAL);
//        Font normalFont = new Font("Cairo-Regular.ttf", 12, Font.NORMAL);


        FontFactory.getFontImp().defaultEncoding = BaseFont.IDENTITY_H;
        LineSeparator lineSep = new LineSeparator();
        LanguageProcessor al = new ArabicLigaturizer();

        try {

            File pdfFolder = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);

//            File pdfFolder = new File(Environment.DIRECTORY_DOWNLOADS, "rapports");
//            if (!pdfFolder.exists()) {
//                pdfFolder.mkdir();
//                Log.i(TAG, "Pdf Directory created");
//            }

            fileName = createFileName();
            File pdfFile = new File(pdfFolder, fileName);
//            File pdfFile = new File(Environment.DIRECTORY_DOWNLOADS,fileName);
            boolean created = true;
            if (!pdfFile.exists()) {
                created = pdfFile.createNewFile();
            }

            if (!created) {
                Toast.makeText(getBaseContext(), "file not created", Toast.LENGTH_SHORT).show();
                Timber.i("file not created");
            }
//            File pdfFile = new File(Environment.DIRECTORY_DOWNLOADS, createFileName());
            OutputStream output = new FileOutputStream(pdfFile);
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, output);
            document.open();
            document.add(new Paragraph("Rapport du jour " + getCurrentDate(format2), darkFont));
            document.add(new Paragraph("Nom : " + fName + " " + lName.toUpperCase(), bigFont));

            // load image
            try {
                // get input stream
                InputStream ims = getAssets().open("logo.png");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
//                image.scaleAbsolute(75f, 75f);
                document.add(image);
            } catch (Exception ex) {
                Timber.e(ex.getMessage());
            }

//            Paragraph title2 = new Paragraph("This is Chapter 2", FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLDITALIC, new Color(0, 0, 255)));
//            Chapter chapter2 = new Chapter(title2, 2);
//            chapter2.setNumberDepth(0);
//            Paragraph someText = new Paragraph("This is some text");
//            chapter2.add(someText);
//            Paragraph title21 = new Paragraph("This is Section 1 in Chapter 2", FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new Color(255, 0, 0)));
//            Section section1 = chapter2.addSection(title21);
//            Paragraph someSectionText = new Paragraph("This is some silly paragraph in a chapter and/or section. It contains some text to test the functionality of Chapters and Section.");
//            section1.add(someSectionText);
//            document.add(Chunk.NEWLINE);

            document.add(new Chunk(lineSep));

            document.add(new Paragraph("Ratio de petit dejeuner : " + rp));
            document.add(new Paragraph("Ratio de dejeuner       : " + rd));
            document.add(new Paragraph("Ratio de collation      : " + rc));
            document.add(new Paragraph("Ratio de dinner         : " + rdi));
            document.add(new Paragraph("Indice de sensibilite   : " + is));
            document.add(new Paragraph("Objective               : " + obj));

            document.add(new Chunk(lineSep));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(8);
            table.setWidths(new float[] { 1, 2, 1,1,1,1,1,1 });
            table.setHeaderRows(1);
            //--------------------------------------------------------------
            // 1st row (header)
            PdfPCell cell;
//            cell = new PdfPCell(new Phrase("Table du jour",bigFont));
//            cell.setColspan(6);
//            cell.setFixedHeight(30);
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setVerticalAlignment(Element.ALIGN_CENTER);
//            table.addCell(cell);
            //--------------------------------------------------------------
            // 2nd row
            cell = new PdfPCell(new Phrase("Repas", boldFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Aliments", boldFont));
            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("Ratio", boldFont));
            cell = new PdfPCell(new Phrase("Glucide", boldFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Glycemie avant repas", boldFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Unitée a injecter", boldFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Glycemie apres 4 heures", boldFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Dose confirmé", boldFont));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Remarques", boldFont));
            table.addCell(cell);
            //--------------------------------------------------------------
            // 3rd row
            cell = new PdfPCell(new Phrase("Petit dejeuner"));
            table.addCell(cell);

//            PdfPTable t1 = new PdfPTable(3);
//            t1.setWidths(new float[] { 2, 1 });
            PdfPTable t1 = new PdfPTable(2);
            if (a1.length >= 1) {
                PdfPCell ci;
                for (String c : a1) {
                    if (c.isEmpty() || c.equals(" ") || !c.contains(":"))
                        continue;

                    String[] t = c.split(":");

                    ci = new PdfPCell(new Phrase(t[0], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t1.addCell(ci);

                    ci = new PdfPCell(new Phrase(t[1], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t1.addCell(ci);
                }
                cell = new PdfPCell(t1);
            } else {
                cell = new PdfPCell(new Phrase(alimentsPDej, normalFont));
            }
            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("" + rp));
            cell = new PdfPCell(new Phrase("" + gluco0));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r1));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r2));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r3));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r4));
            table.addCell(cell);
            (cell = new PdfPCell(new Phrase("" + c1, normalFont))).setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.addCell(cell);

            //--------------------------------------------------------------
            // 4th row
            cell = new PdfPCell(new Phrase("Dejeuner"));
            table.addCell(cell);

            PdfPTable t2 = new PdfPTable(2);
            if (a2.length >= 1) {
                PdfPCell ci;
                for (String c : a2) {
                    if (c.isEmpty() || c.equals(" ") || !c.contains(":"))
                        continue;

                    String[] t = c.split(":");

                    ci = new PdfPCell(new Phrase(t[0], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t2.addCell(ci);

                    ci = new PdfPCell(new Phrase(t[1], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t2.addCell(ci);
                }
                cell = new PdfPCell(t2);
            } else {
                cell = new PdfPCell(new Phrase(alimentsDej, normalFont));
            }
            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("" + rd));
            cell = new PdfPCell(new Phrase("" + gluco1));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r5));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r6));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r7));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r8));
            table.addCell(cell);
            (cell = new PdfPCell(new Phrase("" + c2, normalFont))).setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.addCell(cell);
            //--------------------------------------------------------------
            // 5th row
            cell = new PdfPCell(new Phrase("Collation"));
            table.addCell(cell);

            PdfPTable t3 = new PdfPTable(2);
            if (a3.length >= 1) {
                PdfPCell ci;
                for (String c : a3) {
                    if (c.isEmpty() || c.equals(" ") || !c.contains(":"))
                        continue;

                    String[] t = c.split(":");

                    ci = new PdfPCell(new Phrase(t[0], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t3.addCell(ci);

                    ci = new PdfPCell(new Phrase(t[1], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t3.addCell(ci);
                }
                cell = new PdfPCell(t3);
            } else {
                cell = new PdfPCell(new Phrase(alimentsCol, normalFont));
            }
            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("" + rc));
            cell = new PdfPCell(new Phrase("" + gluco2));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r9));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r10));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r11));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r12));
            table.addCell(cell);
            (cell = new PdfPCell(new Phrase("" + c3, normalFont))).setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.addCell(cell);
            //--------------------------------------------------------------
            // 6th row
            cell = new PdfPCell(new Phrase("Dinner"));
            table.addCell(cell);
            PdfPTable t4 = new PdfPTable(2);
            if (a4.length >= 1) {
                PdfPCell ci;
                for (String c : a4) {
                    if (c.isEmpty() || c.equals(" ") || !c.contains(":"))
                        continue;

                    String[] t = c.split(":");

                    ci = new PdfPCell(new Phrase(t[0], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t4.addCell(ci);

                    ci = new PdfPCell(new Phrase(t[1], normalFont));
                    ci.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    t4.addCell(ci);
                }
                cell = new PdfPCell(t4);
            } else {
                cell = new PdfPCell(new Phrase(alimentsDin, normalFont));
            }
            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("" + rdi));
            cell = new PdfPCell(new Phrase("" + gluco3));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r13));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r14));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r15));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("" + r16));
            table.addCell(cell);
            (cell = new PdfPCell(new Phrase("" + c4, normalFont))).setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.addCell(cell);
            //--------------------------------------------------------------
            table.setTotalWidth(
                    PageSize.A4.getWidth() - (document.leftMargin() + document.rightMargin()));
            table.setLockedWidth(true);
            document.add(table);
            document.close();

            resetSPValues();
        } catch (Exception e) {
            Timber.e("Exception : " + e.getMessage());
        }

    }

    public void generatePdfServer(View view) {

        String host = sp.getString("host_url", getString(ma.ifdose.app.R.string.host_adr));
        String port = sp.getString("host_port", getString(ma.ifdose.app.R.string.host_port));

        try {
            alimentsPDej = URLEncoder.encode(alimentsPDej, "UTF-8");
            alimentsDej = URLEncoder.encode(alimentsDej, "UTF-8");
            alimentsCol = URLEncoder.encode(alimentsCol, "UTF-8");
            alimentsDin = URLEncoder.encode(alimentsDin, "UTF-8");
            c1 = URLEncoder.encode(alimentsPDej, "UTF-8");
            c2 = URLEncoder.encode(alimentsPDej, "UTF-8");
            c3 = URLEncoder.encode(alimentsPDej, "UTF-8");
            c4 = URLEncoder.encode(alimentsPDej, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Timber.e(e.getMessage());
        }

        String generatePDF = host + ":" + port + getString(ma.ifdose.app.R.string.urlGeneratePDF);
        String params = "?fName=" + fName + "&lName=" + lName + "&email=" + email
                + "&r1=" + r1 + "&r2=" + r2 + "&r3=" + r3 + "&r4=" + r4
                + "&r5=" + r5 + "&r6=" + r6 + "&r7=" + r7 + "&r8=" + r8
                + "&r9=" + r9 + "&r10=" + r10 + "&r11=" + r11 + "&r12=" + r12
                + "&r13=" + r13 + "&r14=" + r14 + "&r15=" + r15 + "&r16=" + r16
                + "&alimentsPDej=" + alimentsPDej + "&alimentsDej=" + alimentsDej
                + "&alimentsCol=" + alimentsCol + "&alimentsDin=" + alimentsDin
                + "&gluco0=" + gluco0 + "&gluco1=" + gluco1
                + "&gluco2=" + gluco2 + "&gluco3=" + gluco3
                + "&c1" + c1 +"&c2" + c2 +"&c3" + c3 +"&c4" + c4
                + "&rp=" + rp + "&rd=" + rd + "&rc=" + rc
                + "&rdi=" + rdi + "&is=" + is + "&obj=" + obj;

        generatePDF += params;

        Timber.i("Visiting URL " + generatePDF);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, generatePDF, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            Timber.i("Status : " + status);
                        } catch (JSONException e) {
                            Timber.e(e.getMessage());
                        }
                        String res = getString(ma.ifdose.app.R.string.report_sent_succeded);
                        Toast.makeText(getBaseContext(), res, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
//                        String response = error.getMessage();
                        String res = getString(ma.ifdose.app.R.string.report_sent_failed);
                        Toast.makeText(getBaseContext(), res, Toast.LENGTH_LONG).show();
                        Timber.e(error.getMessage());
                    }


                });

        queue.add(postRequest);


    }

    public String createFileName() {

        String fileName = "";
        String ext = ".pdf";

        // patient name
        String patientName = fName + " " + lName;

        fileName = patientName + " _ " + getCurrentDate(format) + ext;

        return fileName;
    }

    public String getCurrentDate(String format) {
        //            return DateFormat.getDateTimeInstance().format(new Date())
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c.getTime());
    }

}
