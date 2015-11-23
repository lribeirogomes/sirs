package pt.ulisboa.tecnico.meic.sirs.group6.securesms.domain;

import java.security.Key;
import java.util.Map;

/**
 * Created by lribeirogomes on 23/11/15.
 */
public class User {
    private String phoneNumber;
    private Key priEncryptKey,
                priSignKey;
    private Map<String, Contact> contacts;
}
