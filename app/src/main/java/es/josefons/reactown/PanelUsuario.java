package es.josefons.reactown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class PanelUsuario extends Fragment {

    Button btnPanelUsuarioPassword, btnPanelUsuarioBorrar;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FirebaseUser usuario;
    private View vista;

    public PanelUsuario() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = container;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panel_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPanelUsuarioPassword = view.findViewById(R.id.btnPanelUsuarioPassword);
        btnPanelUsuarioBorrar = view.findViewById(R.id.btnPanelUsuarioBorrar);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usuario = FirebaseAuth.getInstance().getCurrentUser();

        btnPanelUsuarioPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioPass(v);
            }
        });

        btnPanelUsuarioBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarCuenta(v);
            }
        });
    }

/*
    private void cambioCorreo(View v) {
        if(!etPanelUsuarioCorreo.getText().toString().isEmpty()){
            AlertDialog alertbox = new AlertDialog.Builder(v.getContext())
                    .setMessage("¿Quieres cambiar el correo?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            FirebaseUser actualUser = mAuth.getCurrentUser();
                            actualUser.updateEmail(etPanelUsuarioCorreo.getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            updateEnBaseDeDatos(etPanelUsuarioCorreo.getText().toString());
                                            cambiarAutorPost();
                                            Toast.makeText(getContext(), "Correo cambiado.", Toast.LENGTH_LONG).show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ///mAuth.signOut();
                                                    //Navigation.findNavController(vista).navigate(R.id.panelUsuarioPassword);
                                                    Navigation.findNavController(vista).navigate(R.id.volverPanelUsuario);
                                                }
                                            }, 1500);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "No se ha podido actualizar", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            etPanelUsuarioCorreo.setText("");
                        }
                    })
                    .show();
        } else {
            Toast.makeText(v.getContext(), "Completa el correo para cambiarlo", Toast.LENGTH_SHORT).show();
        }
    }
*/
    /**
     * Funcion unicamente dedicada al cambio de contraseña. Se cerrara sesion para que el usuario pueda
     * iniciar con la nueva contraseña.
     * @param v
     */
    private void cambioPass(View v){
        AlertDialog alertbox = new AlertDialog.Builder(v.getContext())
                .setMessage("¿Quieres cambiar contraseña?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Correo enviado, revisa tu correo.", Toast.LENGTH_LONG).show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAuth.signOut();
                                                    Navigation.findNavController(vista).navigate(R.id.panelUsuarioPassword);
                                                }
                                            }, 1500);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Fallo en actualizar la contraseña", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //
                    }
                })
                .show();

    }

/*
    private void updateEnBaseDeDatos(String correo) {
        DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        updateData.child("correo").setValue(correo);
    }
*/
    /**
     * Borrar el usuario
     * @param v
     */
    private void borrarCuenta(View v){

        AlertDialog alertbox = new AlertDialog.Builder(v.getContext())
                .setMessage("¿Estas seguro? Esta opcion es irreversible")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        usuario.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Usuario borrado. Adios!", Toast.LENGTH_SHORT).show();
                                    mDatabase.child("Users").child(usuario.getUid()).removeValue();
                                    mAuth.signOut();
                                    Navigation.findNavController(vista).navigate(R.id.panelUsuarioPassword);
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        //
                    }
                })
                .show();
    }

/*
    private void cambiarAutorPost(){
        mDatabase.child("itemListado").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String autor = snapshot.child("propuestaUsuario").getValue().toString().trim();
                        if(autor.equals(correoActual)){
                            cambiarAutorKey(snapshot.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }


    private void cambiarAutorKey(String key) {
        DatabaseReference updateData = FirebaseDatabase.getInstance()
                .getReference("itemListado").child(key);
        updateData.child("propuestaUsuario").setValue(etPanelUsuarioCorreo.getText().toString());
    }
*/
}
