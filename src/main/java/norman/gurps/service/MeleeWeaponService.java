package norman.gurps.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.model.equipment.MeleeWeapon;
import norman.gurps.model.equipment.WeaponSkill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeleeWeaponService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeleeWeaponService.class);
    public static final String MELEE_WEAPONS_RESOURCE = "data/melee-weapons.json";
    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<MeleeWeapon> findAll() {
        return loadMeleeWeapons();
    }

    private static List<MeleeWeapon> loadMeleeWeapons() {
        List<MeleeWeapon> meleeWeaponList = new ArrayList<>();
        try {
            InputStream stream = loader.getResourceAsStream(MELEE_WEAPONS_RESOURCE);
            MeleeWeapon[] meleeWeaponArray = mapper.readValue(stream, MeleeWeapon[].class);
            meleeWeaponList.addAll(Arrays.asList(meleeWeaponArray));
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    "Error loading meleeWeapons from resource " + MELEE_WEAPONS_RESOURCE + ".", e);
        }
        return meleeWeaponList;
    }

    public static List<WeaponSkill> findWeaponSkills(String weaponName) {
        List<WeaponSkill> skills = new ArrayList<>();
        List<MeleeWeapon> weapons = loadMeleeWeapons();
        for (MeleeWeapon weapon : weapons) {
            if (weapon.getName().equals(weaponName)) {
                skills.addAll(weapon.getSkills());
            }
        }
        return skills;
    }
}
