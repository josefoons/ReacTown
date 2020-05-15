package es.josefons.reactown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.josefons.reactown.objetos.ItemListado;
import es.josefons.reactown.objetos.Usuario;

public class Listado extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private TextView tvInformacionUsuarioListado;
    private Usuario usuario;
    int Gallary_intent = 2000;
    Uri imagenUri;
    StorageTask mUploadTask;
    StorageReference mStorageRef;
    DatabaseReference mDatabaseRef;

    ImageButton btnImagenMain;
    RecyclerView recyclerView;
    ItemListadoAdapter itemListadoAdapter;
    List<ItemListado> listadoList;

    public Listado() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog alertbox = new AlertDialog.Builder(getView().getContext())
                        .setMessage("¿Quieres salir de la cuenta?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mAuth.signOut();
                                Navigation.findNavController(getView()).navigate(R.id.logout);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //
                            }
                        })
                        .show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_listado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnImagenMain = view.findViewById(R.id.btnImagenMain);
        tvInformacionUsuarioListado = view.findViewById(R.id.tvInformacionUsuarioListado);
        getUserInfo();

        /* Recycler */
        listadoList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        itemListadoAdapter = new ItemListadoAdapter(listadoList, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemListadoAdapter);

        mDatabase.child("itemListado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listadoList.removeAll(listadoList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemListado aux = new ItemListado();
                    aux.setId(snapshot.getKey());
                    aux.setIcon(snapshot.child("propuestaImagen").getValue().toString());
                    aux.setName(snapshot.child("propuestaNombre").getValue().toString());
                    listadoList.add(aux);
                }
                itemListadoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itemListadoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle datos = new Bundle();
                String aux = listadoList.get(recyclerView.getChildAdapterPosition(v)).getId();
                datos.putString("id", aux);
                datos.putInt("perm", usuario.getPermiso());
                Navigation.findNavController(getView()).navigate(R.id.infoRecycler, datos);
            }
        });

        /* Recycler */

        mStorageRef = FirebaseStorage.getInstance().getReference("imgPerfil");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        btnImagenMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Gallary_intent);
            }
        });
    }

    /**
     * Funcion dedicada a subir la imagen de perfil que el usuario quiera y "linkearla" al usuario en su
     * base de datos. Esta imagen se le pondra el mismo nombre asi se sustituira automaticamente en el almacenamiento
     */
    private void subirImagenPerfil(){
        if(imagenUri != null) {
            //Damos el mismo nombre siempre para que se sustituya solo la imagen.
            StorageReference fileReference = mStorageRef.child("perfil_" + mAuth.getCurrentUser().getUid() + "." + getFileExtension(imagenUri));

            mUploadTask = fileReference.putFile(imagenUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Subida correctamente", Toast.LENGTH_SHORT).show();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateUser(uri);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Subida fallida", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    /**
     * Cuando un usuario sube por primera vez una imagen, el campo no existe, y por ello se tiene que crear
     * @param uri
     */
    private void updateUser(final Uri uri) {
        final Uri aux = uri;
        mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> valor = new HashMap<String, Object>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            valor.put(snapshot.getKey(), snapshot.getValue());
                        }
                        valor.put("imgPerfil", aux.toString());
                        mDatabaseRef.child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(valor);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Funcion para obtener la extension
     * @param uri
     * @return
     */
    private String getFileExtension(Uri uri) {
        //Obtener file extension
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode,data);
        if(requestCode == Gallary_intent && resultCode == Activity.RESULT_OK) {
            imagenUri = data.getData();
            btnImagenMain.setImageURI(imagenUri);
            subirImagenPerfil();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.menu_listado, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogOut: mAuth.signOut();  Navigation.findNavController(getView()).navigate(R.id.logout); break;
            case R.id.menuAddList:  Navigation.findNavController(getView()).navigate(R.id.anyadirItem);; break;
            case R.id.menuPanelUsuario: Navigation.findNavController(getView()).navigate(R.id.irPanelUsuario); break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Obtener los valores del usuario para utilizarlos
     */
    private void getUserInfo(){
        String UserId = mAuth.getCurrentUser().getUid();
        usuario = new Usuario(UserId, "None", "None", 0, "None");
        usuario.setId(UserId);
        mDatabase.child("Users").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()) {
                     String name = dataSnapshot.child("name").getValue().toString();
                     String correo = dataSnapshot.child("correo").getValue().toString();
                     int perm = Integer.parseInt(dataSnapshot.child("perm").getValue().toString());

                     tvInformacionUsuarioListado.setText(name);
                     usuario.setName(name);
                     usuario.setCorreo(correo);
                     usuario.setPermiso(perm);

                     if (dataSnapshot.child("imgPerfil").exists()) {
                         usuario.setImg(dataSnapshot.child("imgPerfil").getValue().toString());
                         Picasso.get()
                                 .load(dataSnapshot.child("imgPerfil").getValue().toString())
                                 .placeholder(R.drawable.fondo_item_listado)
                                 .centerCrop()
                                 .fit()
                                 .into(btnImagenMain);
                     }
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
