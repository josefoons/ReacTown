package es.josefons.reactown;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class RecuperarPassword extends Fragment {

    EditText correo;
    Button btnVolver, btnRecuperar;
    private FirebaseAuth mAuth;

    public RecuperarPassword() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RecuperarPassword newInstance(String param1, String param2) {
        RecuperarPassword fragment = new RecuperarPassword();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recuperar_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        correo = view.findViewById(R.id.etCorreoRecuperar);
        btnVolver = view.findViewById(R.id.btnVolver);
        btnRecuperar = view.findViewById(R.id.btnRecuperar);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.volverRecuperar);
            }
        });

        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternet()){
                    recuperarPassword();
                }
            }
        });
    }

    private boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            Toast.makeText(getContext(), "NO INTERNET. Revisa la conexion", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void recuperarPassword(){
        mAuth.getInstance().sendPasswordResetEmail(correo.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Correo enviado, revisa tu correo.", Toast.LENGTH_LONG).show();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Navigation.findNavController(getView()).navigate(R.id.volverRecuperar);
                                }
                            }, 1500);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Correo no existe.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
