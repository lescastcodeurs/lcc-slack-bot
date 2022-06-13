package com.lescastcodeurs.bot;

public class ShowNotes {

  public String render() {
    return """
      ---
      title: LCC 999 -\s
      author: 'Emmanuel Bernard'
      team: 'Emmanuel Bernard, Guillaume Laforge, Vincent Massol, Antonio Goncalves, Arnaud Héritier, Audrey Neveu'
      layout: blog-post
      episode: 999
      mp3_length: 85017000
      tweet: TODO
      # tweet size: 91-93 -> 99-101 #######################################################################
      ---
      Résumé

      Enregistré le 2 janvier 2022

      Téléchargement de l’épisode [LesCastCodeurs-Episode-999.mp3](https://traffic.libsyn.com/lescastcodeurs/LesCastCodeurs-Episode-999.mp3)

      ## News

      ### Langages

      ### Librairies

      ### Infrastructure

      ### Cloud

      ### Web

      ### Data

      ### Outillage

      ### Architecture

      ### Méthodologies

      ### Sécurité

      ### Loi, société et organisation

      ## Outils de l’épisode

      ## Rubrique débutant

      ## Conférences

      [Nom de la conf du x au y mois à Ville]() - [CfP]() jusqu’à y mois \s
      TODO: reprendre celles de l’épisode d’avant

      ## Nous contacter

      Soutenez Les Cast Codeurs sur Patreon <https://www.patreon.com/LesCastCodeurs> \s
      [Faire un crowdcast ou une crowdquestion](https://lescastcodeurs.com/crowdcasting/) \s
      Contactez-nous via twitter <https://twitter.com/lescastcodeurs> \s
      sur le groupe Google <https://groups.google.com/group/lescastcodeurs> \s
      ou sur le site web <https://lescastcodeurs.com/>
      <!-- vim: set spelllang=fr : -->""";
  }
}
