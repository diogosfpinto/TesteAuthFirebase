package com.diogopinto.testeauthfirebase.data;

import android.util.Log;

import com.diogopinto.testeauthfirebase.data.model.LoggedInUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private FirebaseAuth usuario;
    private Result result;
    public LoginDataSource(FirebaseAuth usuario) {
        this.usuario = usuario;
    }

    public Result<LoggedInUser> login(final String username, final String password) {

        try {
            usuario.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Log.i("SignIn", "Usuário logado com sucesso!");
                                result = new Result.Success<>(new LoggedInUser(usuario.getCurrentUser().getUid(),
                                        usuario.getCurrentUser().getEmail()));
                            } else {
//                              Registrar e-mail digitado no Firebase caso ocorra falha no login
                                usuario.createUserWithEmailAndPassword(username, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()){
                                                    Log.i("SignIn",
                                                            "Sucesso ao cadastrar usuário");
                                                    result = new Result.Success<>(
                                                            new LoggedInUser(usuario.getCurrentUser().getUid(),
                                                                    usuario.getCurrentUser().getEmail()));
                                                } else {
                                                    Log.i("SignIn", "Erro ao cadastrar usuário");
                                                    result = new Result.Error
                                                            (new IOException("Falha ao cadastrar usuário"));
                                                }
                                            }
                                        });


                            }
                        }
                    });

//            Verifica usuário logado
            if (usuario.getCurrentUser() != null){
                Log.i("SignIn", "usuario logado: " + usuario.getCurrentUser().getEmail());
            }

            return result;
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        usuario.signOut();
    }
}
