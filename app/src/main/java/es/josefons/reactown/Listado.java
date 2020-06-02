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
import android.widget.Button;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.josefons.reactown.adapters.ItemListadoAdapter;
import es.josefons.reactown.objetos.ItemListado;
import es.josefons.reactown.objetos.Usuario;

public class Listado extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tvInformacionUsuarioListado;
    private Usuario usuario;
    private View vista;
    private int Gallary_intent = 2000;
    private Uri imagenUri;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private MenuItem mListaUsuarios;
    private ImageButton btnImagenMain;
    private RecyclerView recyclerView;
    private ItemListadoAdapter itemListadoAdapter;
    private List<ItemListado> listadoList;
    private TextView noDatos;
    private Button btnListadoFiltro;

    public Listado() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog alertbox = new AlertDialog.Builder(vista.getContext())
                        .setMessage("¿Quieres salir de la cuenta?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                mAuth.signOut();
                                Navigation.findNavController(vista).navigate(R.id.logout);
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
        vista = container;
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
        noDatos = view.findViewById(R.id.noDatos);
        btnListadoFiltro = view.findViewById(R.id.btnListadoFiltro);
        if(mAuth.getCurrentUser() != null){
            getUserInfo();
        } else {
            Navigation.findNavController(vista).navigate(R.id.volver_InfoRecycler);
        }

        /* Recycler */
        listadoList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        itemListadoAdapter = new ItemListadoAdapter(listadoList, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemListadoAdapter);

        cargarRecyclerAll();

        itemListadoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle datos = new Bundle();
                String aux = listadoList.get(recyclerView.getChildAdapterPosition(v)).getId();
                String autorAux = listadoList.get(recyclerView.getChildAdapterPosition(v)).getAutor();
                datos.putString("id", aux);
                datos.putInt("perm", usuario.getPermiso());
                datos.putString("email", autorAux);
                Navigation.findNavController(vista).navigate(R.id.infoRecycler, datos);
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

        btnListadoFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiaFiltro();
            }
        });
    }

    /**
     * Obtener todos los datos de Firebase sin ningun tipo de filtro.
     */
    private void cargarRecyclerAll() {
        mDatabase.child("itemListado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //listadoList.removeAll(listadoList);
                listadoList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemListado aux = new ItemListado();
                        aux.setId(snapshot.getKey());
                        aux.setIcon(snapshot.child("propuestaImagen").getValue().toString());
                        aux.setName(snapshot.child("propuestaNombre").getValue().toString());
                        aux.setAutor(snapshot.child("propuestaUsuario").getValue().toString());
                        listadoList.add(aux);
                    }
                }

                if(listadoList.size() > 0){
                    noDatos.setVisibility(View.GONE);
                    itemListadoAdapter.notifyDataSetChanged();
                } else {
                    noDatos.setVisibility(View.VISIBLE);
                    itemListadoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
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
        mListaUsuarios = menu.findItem(R.id.mListaUsuarios);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Bundle mochila = new Bundle();
        mochila.putInt("perm", usuario.getPermiso());

        switch (item.getItemId()){
            case R.id.menuLogOut: mAuth.signOut(); Navigation.findNavController(vista).navigate(R.id.logout); break;
            case R.id.menuAddList: Navigation.findNavController(vista).navigate(R.id.anyadirItem); break;
            case R.id.menuPanelUsuario: Navigation.findNavController(vista).navigate(R.id.irPanelUsuario); break;
            case R.id.mInfoApp: Navigation.findNavController(vista).navigate(R.id.ir_Informacion); break;
            case R.id.mListaUsuarios: Navigation.findNavController(vista).navigate(R.id.ir_ListaUsuario); break;
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

                     tvInformacionUsuarioListado.setText("Bienvenido, " + name.toUpperCase());
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

                     if(usuario.getPermiso() == 0){
                         mListaUsuarios.setEnabled(false);
                         mListaUsuarios.setVisible(false);
                     }
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Utilidad del boton para cambiar el filtro
     */
    private void cambiaFiltro(){
        String valor = btnListadoFiltro.getText().toString().trim();
        if(valor.equals("Propios")){
            sugerenciasPropias();
            btnListadoFiltro.setText(R.string.btnListadoFiltroTodos);
        } else if(valor.equals("Todos")) {
            cargarRecyclerAll();
            btnListadoFiltro.setText(R.string.btnListadoFiltroPropio);
        }
    }

    /**
     * Cargar sugerencias que el usuario ha creado.
     */
    private void sugerenciasPropias(){
        mDatabase.child("itemListado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listadoList.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String actualAux = snapshot.child("propuestaUsuario").getValue().toString().trim();
                        if(actualAux.equals(mAuth.getCurrentUser().getEmail().trim())) {
                            ItemListado aux = new ItemListado();
                            aux.setId(snapshot.getKey());
                            aux.setIcon(snapshot.child("propuestaImagen").getValue().toString());
                            aux.setName(snapshot.child("propuestaNombre").getValue().toString());
                            aux.setAutor(snapshot.child("propuestaUsuario").getValue().toString());
                            listadoList.add(aux);
                        }
                    }
                }

                if(listadoList.size() > 0){
                    noDatos.setVisibility(View.GONE);
                    itemListadoAdapter.notifyDataSetChanged();
                } else {
                    noDatos.setVisibility(View.VISIBLE);
                    itemListadoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }
}
