package de.lange.Stempeluhr.service;


import de.lange.Stempeluhr.Repository.UsersRepository;
import de.lange.Stempeluhr.model.UsersModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }


    public UsersModel registerUser(String login, String password, String email, String firstName, String lastName){
        if (login == null && password == null){
            return null;
        }else{
            if(usersRepository.findFirstByLogin(login).isPresent()){
                System.out.println("Duplicate login");
                return null;
            }
            UsersModel usersModel = new UsersModel();
            usersModel.setLogin(login);
            usersModel.setPassword(password);
            usersModel.setEmail(email);
            usersModel.setFirstName(firstName);
            usersModel.setLastName(lastName);
            return usersRepository.save(usersModel);
        }
    }
    public UsersModel authenticate (String login, String password){
        return usersRepository.findByLoginAndPassword(login,password).orElse(null);
    }
}
