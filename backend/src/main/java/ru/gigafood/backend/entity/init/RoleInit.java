package ru.gigafood.backend.entity.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ru.gigafood.backend.entity.Role;
import ru.gigafood.backend.repository.RoleRepository;

@Component
public class RoleInit implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInit(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(roleRepository.count() == 0) {
            roleRepository.save(new Role(null, "usr"));
            roleRepository.save(new Role(null, "adm"));
        }
    }
}
