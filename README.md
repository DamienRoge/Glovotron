# Glovotron

Le projet Glovotron a pour but de piloter un robot à 3 roues dont une libre, grâce aux mouvements de la main.
Pour ce faire un microcontroleur Flora couplé à un module de mesure d'acceleration et à un module bluetooth low energie sont installés sur on gant. Les données de mouvement mesurés par le module accélérometre sont communiqué à un téléphone Android qui se charge de les analyser, les transformer en commande moteur simple et les transmettre par bluetooth à une carte arduino qui activement les 2 moteurs à courant continu en fonction du message recu.

Pour plus d'information voyez le diapo de présentation du projet ainsi que les commentaires présents dans les codes sources du projet.

Ce projet est le fruit du travail d'un bînome composé de Luong-Thi-Bien Bossuyt et de Damien Rogé, étudiants en Licence Professionnelle Systèmes Informatiques et Logiciel, spécialité Informatique Distribuée et Systèmes d'information d'Entreprise
