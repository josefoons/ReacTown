package es.josefons.reactown;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.josefons.reactown.objetos.ItemListado;


public class InfoRecycler extends Fragment {
    private String ID_TRAIDO = "";
    private int PERM_TRAIDO = 0;
    private String nombreImagen = "";
    private ImageView imgTotal;
    private TextView titulo, autor, desc, totalVotos;
    private ImageButton btnLike;
    private DatabaseReference mDatabase;
    FirebaseDatabase database;
    private ArrayList<String> todosVotos;
    private Boolean usuarioHaVotado;
    private String idUsuario;
    private int posicionVoto;

    public InfoRecycler() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ID_TRAIDO = bundle.getString("id", "");
            PERM_TRAIDO = bundle.getInt("perm", 0);
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                /*titulo.setText("");
                autor.setText("");
                desc.setText("");
                imgTotal.setImageURI(null);*/
                Navigation.findNavController(getView()).navigate(R.id.volverMainRecycler);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_recycler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        todosVotos = new ArrayList<>();

        imgTotal = view.findViewById(R.id.infoRecyImagen);
        titulo = view.findViewById(R.id.infoRecyNombre);
        autor = view.findViewById(R.id.InfoRecyAutor);
        desc = view.findViewById(R.id.InfoRecyInformacion);
        btnLike = view.findViewById(R.id.infoRecyBtnVoto);
        totalVotos = view.findViewById(R.id.InfoRecyContador);

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                votarPropuesta();
            }
        });

        cargarInfo();
    }

    /**
     * Carga la informacion del apartado mediante el ID que se trae del main. Este comprueba que si existe
     * y si por lo contrario no existe, te devuelve al main.
     */
    private void cargarInfo(){
        DatabaseReference ref = database.getReference("itemListado/" + ID_TRAIDO);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    titulo.setText(dataSnapshot.child("propuestaNombre").getValue().toString());
                    autor.setText(dataSnapshot.child("propuestaUsuario").getValue().toString());
                    desc.setText(dataSnapshot.child("propuestaDescripcion").getValue().toString());
                    Picasso.get()
                            .load(dataSnapshot.child("propuestaImagen").getValue().toString())
                            .placeholder(R.drawable.placeholder_loading)
                            .centerCrop()
                            .fit()
                            .into(imgTotal);
                    nombreImagen = dataSnapshot.child("propuestaImagen").getValue().toString();
                    for (DataSnapshot snapshot : dataSnapshot.child("propuestaVotos").getChildren()) {
                        todosVotos.add(snapshot.getKey());
                    }
                    totalVotos.setText("+" + todosVotos.size());
                    // Check si el usuario actual ha votado
                    if(todosVotos.contains(FirebaseAuth.getInstance().getCurrentUser().getUid().trim())){
                        usuarioHaVotado = true;
                        posicionVoto = todosVotos.indexOf(FirebaseAuth.getInstance().getCurrentUser().getUid().trim());
                        idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid().trim();
                        btnLike.setImageResource(R.drawable.ic_voto_on);
                    } else {
                        usuarioHaVotado = false;
                        btnLike.setImageResource(R.drawable.ic_voto_off);
                    }
                } else {
                    Toast.makeText(getContext(), "Volviendo...", Toast.LENGTH_SHORT).show();
                    //Navigation.findNavController(getView()).navigate(R.id.volverMainRecycler);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(PERM_TRAIDO == 1){
            inflater.inflate(R.menu.admin_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mDeleteAdmin){
            borrarPost();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * El borrado de post solop tendran acceso los usuarios que tengan el rol de administrador dados
     * desde el control de usuario en la web de firebase.
     */
    private void borrarPost(){
        DatabaseReference ref = database.getReference().child("itemListado").child(ID_TRAIDO);
        ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                borrarImagen();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println(e.getMessage());
            }
        });
        Navigation.findNavController(getView()).navigate(R.id.volverMainRecycler);
    }

    /**
     * Funcion unicamente dedicada al borrado de la imagen cuando un administrador borra el post.
     * Busca la ID de esta y la borra del almacenamiento
     */
    private void borrarImagen(){
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(nombreImagen);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("Imagen borrada");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Error al borrar.");
            }
        });
    }

    private void votarPropuesta(){
        if(usuarioHaVotado){
            // SI ha votado, y lo quita
            DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("itemListado")
                    .child(ID_TRAIDO).child("propuestaVotos").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            updateData.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    btnLike.setImageResource(R.drawable.ic_voto_off);
                    usuarioHaVotado = false;
                    if(todosVotos.size() > 0){
                        todosVotos.remove(posicionVoto);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //NO ha votado, y lo pone
            final DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("itemListado")
                    .child(ID_TRAIDO).child("propuestaVotos");
            updateData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, Object> valor = new HashMap<String, Object>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                valor.put(snapshot.getKey(), snapshot.getValue());
                            }
                            valor.put(FirebaseAuth.getInstance().getUid(), "yes");
                            updateData.updateChildren(valor);
                            usuarioHaVotado = true;
                            todosVotos.add(FirebaseAuth.getInstance().getUid());
                            btnLike.setImageResource(R.drawable.ic_voto_on);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        actualizarVotos();
    }

    private void actualizarVotos(){
        DatabaseReference ref = database.getReference("itemListado/" + ID_TRAIDO);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    todosVotos.clear();
                    for (DataSnapshot snapshot : dataSnapshot.child("propuestaVotos").getChildren()) {
                        todosVotos.add(snapshot.getKey());
                    }
                    totalVotos.setText("+" + todosVotos.size());
                } else {
                    Toast.makeText(getContext(), "Error al actualizar votos...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}
