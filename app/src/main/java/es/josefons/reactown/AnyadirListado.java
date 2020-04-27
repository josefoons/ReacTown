package es.josefons.reactown;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

public class AnyadirListado extends Fragment {

    //https://www.youtube.com/watch?v=6w4bx76Mgoc

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    int Gallary_intent = 2000;
    Button btnSubirImagen, btnInsertarDescripcion;
    ImageView imagenSubir;
    EditText etNombreSugerencia, etDescripcionSugerencia;
    ProgressDialog progressDialog;

    public AnyadirListado() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anyadir_listado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        btnSubirImagen = getView().findViewById(R.id.btnSubirImagen);
        btnInsertarDescripcion = getView().findViewById(R.id.btnInsertarDescripcion);
        imagenSubir = getView().findViewById(R.id.imagenSugerenciaSubir);
        etNombreSugerencia = getView().findViewById(R.id.etNombreSugerencia);
        etDescripcionSugerencia = getView().findViewById(R.id.etDescripcionSugerencia);
        progressDialog = new ProgressDialog(getContext());


        btnSubirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Gallary_intent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode == Gallary_intent && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            imagenSubir.setImageURI(uri);
        }
    }
}
