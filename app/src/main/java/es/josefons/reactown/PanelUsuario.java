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

    Button btnPanelUsuarioCorreo, btnPanelUsuarioPassword;
    EditText etPanelUsuarioCorreo;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panel_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPanelUsuarioCorreo = view.findViewById(R.id.btnPanelUsuarioCorreo);
        btnPanelUsuarioPassword = view.findViewById(R.id.btnPanelUsuarioPassword);
        etPanelUsuarioCorreo = view.findViewById(R.id.etPanelUsuarioCorreo);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnPanelUsuarioPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioPass(v);
            }
        });

        btnPanelUsuarioCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioCorreo(v);
            }
        });
    }

    /**
     * Cambiar el correo del usuario mediante el sistema automatico de Firebase.
     * @param v
     */
    private void cambioCorreo(View v) {
        if(!etPanelUsuarioCorreo.getText().toString().isEmpty()){
            AlertDialog alertbox = new AlertDialog.Builder(v.getContext())
                    .setMessage("¿Quieres cambiar el correo?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            FirebaseUser actualUser = mAuth.getCurrentUser();
                            actualUser.updateEmail(etPanelUsuarioCorreo.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            updateEnBaseDeDatos(etPanelUsuarioCorreo.getText().toString());
                                            Toast.makeText(getContext(), "Correo cambiado.", Toast.LENGTH_LONG).show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    //mAuth.signOut();
                                                    Navigation.findNavController(getView()).navigate(R.id.volverPanelUsuario);
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
                                                    Navigation.findNavController(getView()).navigate(R.id.panelUsuarioPassword);
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

    private void updateEnBaseDeDatos(String correo) {
        DatabaseReference updateData = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        updateData.child("correo").setValue(correo);
    }
}
