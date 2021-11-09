package norman.gurps;

import norman.gurps.character.GameCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainNew {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainNew.class);

    public static void main(String[] args) {
        LOGGER.debug("Starting Application");
        MainNew me = new MainNew();
        try {
            me.doIt(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.debug("Finished Application");
    }

    private void doIt(String[] args) throws IOException {
        // Create characters.
        List<GameCharacter> gameCharacters = new ArrayList<>();
        System.out.println("gameCharacters=" + gameCharacters);
        // Add characters to combat.
        // Loop until combat is over, ...
        //   If actor falls unconscious, ...
        //     Change actor health status.
        //   If actor falls unconscious, ...
        //     Change actor health status.
        //   If actor is unconscious or dead, ...
        //     Skip rest of this loop.
        //   Do free posture change.
        //   Chose action.
        //   If required by action, ...
        //     Chose target.
        //   Loop until action(s) executed, ...
        //     Set ATTACK_SUCCESS to FALSE.
        //     Set DEFENSE_SUCCESS to FALSE.
        //     If action is not attack, ...
        //       Do something and skip rest of loop.
        //     If attack is a critical failure, ...
        //       Do something and skip rest of loop.
        //     If attack is an ordinary failure, ...
        //       Skip rest of loop.
        //     If attack is a critical success, ...
        //       Set ATTACK_SUCCESS to TRUE.
        //     If attack is an ordinary success, ...
        //       Set ATTACK_SUCCESS to TRUE.
        //       Defender chooses defense.
        //       Loop until defense(s) executed or a defense succeeded, ...
        //         If defense is a failure, ...
        //           Skip rest of loop.
        //         If defense is a success, ...
        //           Set DEFENSE_SUCCESS to TRUE.
        //           If defense does damage against attack, ...
        //             Do damage to attacker.
        //             Does attacker die from damage?
        //     If ATTACK_SUCCESS is TRUE and DEFENSE_SUCCESS is FALSE, ...
        //       Do damage to defender.
        //       Does defender die from damage?
    }
}
