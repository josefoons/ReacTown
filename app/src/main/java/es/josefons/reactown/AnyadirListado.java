package es.josefons.reactown;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.josefons.reactown.objetos.UploadItemListado;

public class AnyadirListado extends Fragment {
    //https://www.youtube.com/watch?v=lPfQN-Sfnjw

    FirebaseAuth mAuth;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;
    StorageTask mUploadTask;
    Uri imagenUri;

    int Gallary_intent = 2000;
    Button btnInsertarDescripcion;
    ImageView imagenSubir;
    EditText etNombreSugerencia, etDescripcionSugerencia;
    ProgressBar procesoSubida;

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
        btnInsertarDescripcion = getView().findViewById(R.id.btnInsertarDescripcion);
        imagenSubir = getView().findViewById(R.id.imagenSugerenciaSubir);
        etNombreSugerencia = getView().findViewById(R.id.etNombreSugerencia);
        etDescripcionSugerencia = getView().findViewById(R.id.etDescripcionSugerencia);
        procesoSubida = getView().findViewById(R.id.procesoSubida);

        mStorageRef = FirebaseStorage.getInstance().getReference("imgItems");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("itemListado");

        imagenSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Gallary_intent);
            }
        });

        btnInsertarDescripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask != null && mUploadTask.isInProgress())  {
                    Toast.makeText(getContext(), "Subida en progreso", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode == Gallary_intent && resultCode == Activity.RESULT_OK) {
            imagenUri = data.getData();
            imagenSubir.setImageURI(imagenUri);
        }
    }

    /**
     * Funcion para obtener la extension de la imagen y poderla subir con su extension correcta.
     * @param uri Resultado de la imagen
     * @return devuelve el varlor de la imagen
     */
    private String getFileExtension(Uri uri) {
        //Obtener file extension
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Mediante la comprobacion de los campos del formulario sube la imagen al storage y cuando esto es completado,
     * inserta el enlace de la imagen a la base de datos para asi poderla cargar.
     * Tambien se ha creado una barra de carga para la subida de la imagen. En el momento esta completada la subida,
     * se procede a redireccionar al usuario al main.
     */
    private void uploadFile() {
        if(imagenUri != null && !etNombreSugerencia.getText().toString().isEmpty() && !etDescripcionSugerencia.getText().toString().isEmpty()) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imagenUri));

            mUploadTask = fileReference.putFile(imagenUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Subida correctamente", Toast.LENGTH_SHORT).show();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UploadItemListado upload = new UploadItemListado();
                                    upload.setPropuestaNombre(etNombreSugerencia.getText().toString().trim());
                                    upload.setPropuestaDescripcion(etDescripcionSugerencia.getText().toString().trim());
                                    upload.setPropuestaImagen(uri.toString());
                                    upload.setPropuestaUsuario(user.getEmail());
                                    Map<String, String> valor = new HashMap<String, String>();
                                    valor.put(FirebaseAuth.getInstance().getUid().toString(), "yes");
                                    upload.setPropuestaVotos(valor);

                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                }
                            });

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Navigation.findNavController(getView()).navigate(R.id.AnyadidoCompletado);
                                }
                            }, 1500);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()); // Para calcular el progreso de la subida
                            procesoSubida.setProgress((int) progress);

                        }
                    });
        } else {
            Toast.makeText(getContext(), "Completa los campos.", Toast.LENGTH_LONG).show();
        }
    }
}
