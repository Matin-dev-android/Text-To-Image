package com.matinApplication.texttoimage;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.autofill.AutofillId;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.prefs.Preferences;


public class TextActivity extends AppCompatActivity {

    Bundle extru;
    TextView TvReplace;
    Button save;
    ConstraintLayout ColorSelector, ColorSelectorForBackgrand;
    ScrollView scrollView;
    RelativeLayout relativeLayout;
    SeekBar seekBar;
    private Uri userSelectedFolderUri = null;
    public String fileFormat = "jpg";
    public String fileName = "Image";
    String saveFolder = "/متن به عکس";
    String TAG = "LogCatTags";

    // Developed by Matin

    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 1001;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity2_text);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main2_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        TvReplace = findViewById(R.id.TvReplace);
        extru = getIntent().getExtras();
        if (extru != null) {
            TvReplace.setText(extru.getString("teext"));
        }

        SharedPreferences preferences1 = getSharedPreferences("name", MODE_PRIVATE);
        if (preferences1.getString("inputFileName", "def") == "def") {
            fileName = "Image";
            Log.i("PRF_INFORMATION", "Not Found File Name Saved...");
        } else {
            fileName = preferences1.getString("inputFileName", "Image");
            Log.i("PRF_INFORMATION", "Saved File Name Founded !");
        }
        SharedPreferences preferences = getSharedPreferences("saveFileLocation", MODE_PRIVATE);

        String savedUriString = preferences.getString("savedUri", null);
        String savedPathString = preferences.getString("savedLocation", null);

        if (savedUriString != null) {
            try {
                userSelectedFolderUri = Uri.parse(savedUriString);
            } catch (Exception e) {
                Log.e(TAG, "خطا در خواندن URI ذخیره‌شده", e);
            }
            saveFolder = null;
        } else if (savedPathString != null) {
            saveFolder = savedPathString;
            userSelectedFolderUri = null;
        } else {

            saveFolder = Environment.DIRECTORY_PICTURES;
            userSelectedFolderUri = null;
        }



        save = findViewById(R.id.SaveBtn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveScrollViewAsImage(TvReplace, scrollView, saveFolder, fileName, fileFormat);
            }
        });
        scrollView = findViewById(R.id.Scroll);
        relativeLayout = findViewById(R.id.Relative);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            try {
                String text = readTextFromUri(uri);
                TvReplace.setText(text);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "خطا در خواندن فایل", Toast.LENGTH_SHORT).show();
            }
        }



        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                TvReplace.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ColorSelector = findViewById(R.id.colorChoiser);
        ColorSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialColorPickerDialog
                        .Builder(TextActivity.this)
                        .setTitle("انتخاب رنگ")
                        .setColorShape(ColorShape.CIRCLE)
                        .setColorSwatch(ColorSwatch._500)
                        .setDefaultColor("#000")
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int color, @NotNull String colorHex) {
                                // Handle Color Selection
                                TvReplace.setTextColor(color);
                            }
                        })
                        .show();



            }
        });
        ColorSelectorForBackgrand = findViewById(R.id.ColorChoosrForBackgrand);
        ColorSelectorForBackgrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialColorPickerDialog
                        .Builder(TextActivity.this)
                        .setTitle("انتخاب رنگ زمینه :")
                        .setColorShape(ColorShape.CIRCLE)
                        .setColorSwatch(ColorSwatch._300)
                        .setColorListener(new ColorListener() {
                            @Override
                            public void onColorSelected(int i, @NonNull String s) {
                                relativeLayout.setBackgroundColor(i);
                                scrollView.setBackgroundColor(i);
                            }
                        })
                        .show();
            }
        });
    }

    public void saveScrollViewAsImage(View viewForWidthAndHeight, ScrollView scrollViewForImage, String requestedFolderName, String inputFileName, String selectedFileFormat) {
        if (!checkAndRequestStoragePermission()) {
            Log.w(TAG, "Storage permission not granted yet.");
            return;
        }

        View content = scrollViewForImage.getChildAt(0);
        if (content == null) {
            Toast.makeText(scrollViewForImage.getContext(), "محتوایی برای ذخیره یافت نشد.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.getHeight() > 10000) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("خطا در هسته پردازشی")
                    .setMessage("محتوای وارد شده بسیاز طولانی میباشد و حجم پردازش مورد نیاز بیشتر از توان دستگاه میباشد ، لطفا از محتوای کوتاه تری استفاده کنید")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setCancelable(false)
                    .create().show();


            return;
        }


        Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        content.draw(canvas);

        Bitmap.CompressFormat compressFormat;
        String fileExtension = selectedFileFormat.toLowerCase(Locale.US);
        int quality = 100;

        switch (fileExtension) {
            case "png":
                compressFormat = Bitmap.CompressFormat.PNG;
                fileExtension = "png";
                break;
            case "webp":
                compressFormat = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ?
                        Bitmap.CompressFormat.WEBP_LOSSY : Bitmap.CompressFormat.WEBP;
                fileExtension = "webp";
                break;
            case "jpg":
                compressFormat = Bitmap.CompressFormat.JPEG;
                fileExtension = "jpg";
                break;
            case "jpeg":
                compressFormat = Bitmap.CompressFormat.JPEG;
                fileExtension= "jpeg";
                break;
            case "bmp":
                compressFormat = Bitmap.CompressFormat.PNG;
                fileExtension = "bmp";
                break;
            case "gif":
                compressFormat = Bitmap.CompressFormat.PNG;
                fileExtension = "gif";
                break;
            default:
                compressFormat = Bitmap.CompressFormat.JPEG;
                fileExtension = "jpg";
                Toast.makeText(scrollViewForImage.getContext(), "فرمت ناشناخته، ذخیره با JPG.", Toast.LENGTH_SHORT).show();
                break;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String finalFileName = (inputFileName.isEmpty() ? "Image" : inputFileName) + "_" + timeStamp + "." + fileExtension;

        OutputStream fos = null;
        Context context = scrollViewForImage.getContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (userSelectedFolderUri != null) {
                DocumentFile pickedDir = DocumentFile.fromTreeUri(context, userSelectedFolderUri);
                if (pickedDir != null && pickedDir.canWrite()) {
                    DocumentFile newFile = pickedDir.createFile("image/" + fileExtension, finalFileName);
                    try {
                        fos = context.getContentResolver().openOutputStream(newFile.getUri());
                        if (fos != null) {
                            bitmap.compress(compressFormat, quality, fos);
                            fos.flush();
                            Toast.makeText(context, "فایل در پوشه انتخاب‌شده ذخیره شد.", Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "خطا در ذخیره در پوشه انتخابی:", e);
                        Toast.makeText(context, "ذخیره در پوشه انتخاب‌شده شکست خورد.", Toast.LENGTH_SHORT).show();
                    } finally {
                        try {
                            if (fos != null) fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(context, "دسترسی نوشتن در پوشه انتخابی وجود ندارد.", Toast.LENGTH_SHORT).show();
                }
            } else {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, finalFileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + fileExtension);
                values.put(MediaStore.Images.Media.RELATIVE_PATH, requestedFolderName.isEmpty() ?
                        Environment.DIRECTORY_PICTURES : requestedFolderName);
                values.put(MediaStore.Images.Media.IS_PENDING, 1);

                Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                Uri imageUri = context.getContentResolver().insert(collection, values);

                if (imageUri != null) {
                    try {
                        fos = context.getContentResolver().openOutputStream(imageUri);
                        if (fos != null) {
                            bitmap.compress(compressFormat, quality, fos);
                            fos.flush();
                            values.clear();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            context.getContentResolver().update(imageUri, values, null, null);
                            String s = "فایل در "+requestedFolderName+" ذخیره شد" ;
                            Toast.makeText(context, s , Toast.LENGTH_LONG).show();

                        }
                    } catch (IOException e) {
                        Log.e(TAG, "خطا در ذخیره سازی با MediaStore: ", e);
                        context.getContentResolver().delete(imageUri, null, null);
                    } finally {
                        try {
                            if (fos != null) fos.close();
                        } catch (IOException e) {
                            Log.e(TAG, "خطا در بستن خروجی: ", e);
                        }
                    }
                }
            }
        } else {
            File directory;
            if (requestedFolderName.startsWith(File.separator)) {
                directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + requestedFolderName);
            } else {
                directory = Environment.getExternalStoragePublicDirectory(requestedFolderName);
            }

            if (!directory.exists() && !directory.mkdirs()) {
                Log.e(TAG, "خطا در ایجاد پوشه: " + directory.getAbsolutePath());
                Toast.makeText(context, "خطا در ایجاد پوشه.", Toast.LENGTH_SHORT).show();
                return;
            }

            File imageFile = new File(directory, finalFileName);
            try {
                fos = new FileOutputStream(imageFile);
                bitmap.compress(compressFormat, quality, fos);
                fos.flush();
                Toast.makeText(context, "فایل در: " + imageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e(TAG, "خطا در ذخیره‌سازی فایل: ", e);
                Toast.makeText(context, "خطا در ذخیره فایل.", Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    if (fos != null) fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "خطا در بستن خروجی: ", e);
                }
            }
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.format_item) {

            //------------------------

            String[] items = {"png", "jpg", "webp", "jpeg", "bmp", "gif"};

            new MaterialAlertDialogBuilder(TextActivity.this)
                    .setTitle("فرمت ذخیره سازی را انتخاب کنید :")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fileFormat = items[which];
                            Toast.makeText(TextActivity.this, "ذخیره میشود با : " + items[which], Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("لغو",null)
                    .create().show();


            // ------------------
        } else if (item.getItemId() == R.id.fileSaveName) {
            AlertDialog.Builder bilder = new AlertDialog.Builder(TextActivity.this);
            bilder.setTitle("نام فایل را وارد کنید :");
            EditText editText = new EditText(this);
            editText.setHint("نام فایل");
            bilder.setView(editText);
            bilder.setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (editText.getText().toString().length() > 20) {
                        Toast.makeText(TextActivity.this, "طول متن نباید بیشتر از 20 کاراکتر باشد", Toast.LENGTH_LONG).show();
                    } else {
                        SharedPreferences preferences1 = getSharedPreferences("name", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences1.edit();
                        editor.putString("inputFileName", editText.getText().toString()).apply();
                        Toast.makeText(TextActivity.this, "Saved", Toast.LENGTH_LONG).show();
                        fileName = editText.getText().toString();
                    }
                }
            });
            bilder.setNegativeButton("لغو", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            bilder.create().show();


        } else if (item.getItemId() == R.id.saveLocations_Item) {
            String[] locations = {"در گالری پوشه متن به عکس", "در پوشه pictures", "محل دلخواه ( بزودی )"};

            new MaterialAlertDialogBuilder(TextActivity.this)
                    .setTitle("محل ذخیره سازی ر انتخاب کنید :")
                    .setItems(locations, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                SharedPreferences preferences = getSharedPreferences("saveFileLocation", MODE_PRIVATE);
                                saveFolder = Environment.DIRECTORY_PICTURES+"/متن به عکس";
                                userSelectedFolderUri = null;
                                preferences.edit().putString("savedLocation", saveFolder).apply();
                                Toast.makeText(TextActivity.this, "در گالری ذخیره میشود", Toast.LENGTH_LONG).show();
                            } else if (which == 1) {
                                SharedPreferences preferences = getSharedPreferences("saveFileLocation", MODE_PRIVATE);
                                saveFolder = Environment.DIRECTORY_PICTURES;
                                userSelectedFolderUri = null;
                                preferences.edit().putString("savedLocation", saveFolder).apply();
                                Toast.makeText(TextActivity.this, "در پوشه pictures ذخیره میشود.", Toast.LENGTH_LONG).show();
                            } else if (which == 2) {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                startActivityForResult(intent, 54);
                            }
                        }
                    })
                    .setNegativeButton("لغو",null)
                    .create().show();


            //==========================================
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 54 && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();

            final int takeFlags = data.getFlags() &
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            SharedPreferences preferences = getSharedPreferences("saveFileLocation", MODE_PRIVATE);
            preferences.edit().putString("savedUri", treeUri.toString()).apply();

            userSelectedFolderUri = treeUri;
            saveFolder=null;

            Toast.makeText(this, "پوشه انتخاب شد!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "دسترسی به حافظه داده شد. لطفاً دوباره ذخیره کنید.", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "عدم دسترسی به حافظه. امکان ذخیره فایل وجود ندارد.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new IOException("Unable to open input stream for URI: " + uri.toString());
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append('\n'); // اضافه کردن '\n' برای حفظ خطوط جدید
        }
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }


    // check my github https://github.com/Matin-dev-android
}