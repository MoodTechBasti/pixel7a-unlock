# Pixel 7a recovery artifacts

Hilfsdateien für ein eigenes Pixel 7a (lynx) mit aktivem FRP und gesperrtem
Bootloader. Keine Zugangsdaten, kein Google-Konto.

## REA FRP Flag

`apk/REA-FRP-Flag.apk` setzt nach temporärem Root (GhostLock) das OEM-Unlock-Byte
im Persistent Data Block und erneuert den AOSP-Digest. Signiert mit demselben
Schlüssel wie der DPC (Zertifikat `CN=REA DPC`).

- Paket: `de.rea.frpflag`
- SHA-256: siehe `apk/SHA256SUMS`
- Wirkt nur auf `/dev/block/by-name/frp`, erwartet exakt 524288 Byte.
- Vor dem Schreiben wird die Partition nach `/data/local/tmp` gesichert.

Direktdownload:
`https://github.com/MoodTechBasti/pixel7a-unlock/raw/refs/heads/main/apk/REA-FRP-Flag.apk`

## Unlock DPC

Device-Owner-Fallback. Setzt als Device Owner `adb_enabled=1` und
`adb_wifi_enabled=1`.

- `apk/rea-unlock-dpc.apk` – signiert, Paket `com.rea.unlockdpc`
- `qr/rea-dpc.png` – Provisioning-QR
- `apk/rea.keystore` – Signaturschlüssel, Passwort `rearea`

## Reihenfolge

1. Aus dem Setup heraus Einstellungen erreichbar machen.
2. USB-Debugging aktivieren und ADB autorisieren.
3. GhostLock mit dem exakten Kernel-Profil ausführen.
4. `REA-FRP-Flag.apk` installieren und Flag setzen (oder `setflag.sh` per ADB).
5. `fastboot flashing get_unlock_ability` muss `1` liefern.
6. `fastboot flashing unlock`, dann crDroid.
