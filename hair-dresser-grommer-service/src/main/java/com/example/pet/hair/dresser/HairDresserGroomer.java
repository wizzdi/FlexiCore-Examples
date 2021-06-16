package com.example.pet.hair.dresser;

import com.example.pet.events.PetCreated;
import com.example.pet.interfaces.PetGroomer;
import com.example.pet.model.Pet;
import com.example.pet.response.PetGroomResult;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Extension
@Service
public class HairDresserGroomer implements PetGroomer, Plugin {
    private static final Logger logger= LoggerFactory.getLogger(HairDresserGroomer.class);
    @Override
    public PetGroomResult groom(Pet pet) {
        return new PetGroomResult(getClass().getSimpleName(),30,5,10);
    }

    @EventListener
    public void onPetCreatedEvent(PetCreated petCreated){
        PetGroomResult groom = groom(petCreated.getPet());
        logger.info("pet automatically groomed: "+groom);

    }
}
