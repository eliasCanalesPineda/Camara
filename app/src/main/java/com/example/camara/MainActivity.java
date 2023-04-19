package com.example.camara;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ImageView Logo;
    Button Tomarfoto, correo, whatsapp;
    String nombre;

    private static final int REQUEST_CODI_CAM = 100;
    private static final int REQUEST_CODI_CAP_FOTO = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logo = findViewById(R.id.verfoto);
        Tomarfoto = findViewById(R.id.Tomarfoto);
        correo = findViewById(R.id.Gmail);
        whatsapp = findViewById(R.id.mensaje);
        Tomarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadfoto();
            }
        });
        correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                porcorreo();
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                porwhatsapp();
            }
        });
    }

    public void loadfoto(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            Foto();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, REQUEST_CODI_CAM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODI_CAM){
            if (permissions.length> 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED){
                Foto();
            }else{
                Toast.makeText(MainActivity.this, "Acceso denegado", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void  Foto(){
        Intent Cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Cam.resolveActivity(getPackageManager()) != null){
            startActivityForResult(Cam, REQUEST_CODI_CAP_FOTO);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODI_CAP_FOTO && resultCode == RESULT_OK){
            // Se busca la imagen con la función Bundle.
            Bundle BuscarImagen = data.getExtras();
            // Bitmap se utiliza para cargar laimgen de cualquier archivo.
            Bitmap Buscar_Foto = (Bitmap) BuscarImagen.get("data");
            Logo.setImageBitmap(Buscar_Foto);
            // se obtiene la información de la imagen.
            nombre = "MiImage.jpg";
            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput(nombre, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            Buscar_Foto.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    // Método para enviar la foto por whatsapp.
    public void porwhatsapp(){
        // Se observa la foto una vez tomada con la cámara.
        Logo = findViewById(R.id.verfoto);
        // Se crea el cache de la fotografía tomada con la camara.
        Logo.buildDrawingCache();
        // Se obtiene el cache de la fotografía tomada.
        Bitmap nuevo2 = Logo.getDrawingCache();
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("image/*");
        whatsappIntent.setPackage("com.whatsapp");
        // Se escribe la imagen en formato JPEG.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Se le da calidad a la fotografía tomada.
        nuevo2.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), nuevo2, "Titulo de la imagen", null);
        Uri uri = Uri.parse(path);
        whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(whatsappIntent);
    }
    // Método que almacena enviar la foto por correo electrónico.
    public void porcorreo(){
        Logo = findViewById(R.id.verfoto);
        Logo.buildDrawingCache();
        Bitmap nuevo = Logo.getDrawingCache();
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("image/jpeg");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Mi Fotografía De Mi App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, " ");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        nuevo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), nuevo, "Práctica android", null);
        Uri uri = Uri.parse(path);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Enviar"));
    }
}