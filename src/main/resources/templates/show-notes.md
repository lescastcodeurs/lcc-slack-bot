---
title: LCC {episodeNumber} -
author: 'Emmanuel Bernard'
team: 'Emmanuel Bernard, Guillaume Laforge, Vincent Massol, Antonio Goncalves, Arnaud Héritier, Audrey Neveu, Katia Aresti'
layout: blog-post
episode: {episodeNumber}
youtube: 
mp3_length: 85017000
tweet: TODO
# tweet size: 91-93 -> 99-101 #######################################################################
---
Résumé

Enregistré le {recordDate.format('d MMMM uuuu', locale)}

Téléchargement de l’épisode [LesCastCodeurs-Episode-{episodeNumber}.mp3](https://traffic.libsyn.com/lescastcodeurs/LesCastCodeurs-Episode-{episodeNumber}.mp3)
ou en vidéo [sur YouTube](https://www.youtube.com/@lescastcodeurs).

## News

{#if hasNotes('INCLUDE')}
### Non catégorisées

{#for note in notes('INCLUDE')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('LANGUAGES')}
### Langages

{#for note in notes('LANGUAGES')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('LIBRARIES')}
### Librairies

{#for note in notes('LIBRARIES')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('INFRASTRUCTURE')}
### Infrastructure

{#for note in notes('INFRASTRUCTURE')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('CLOUD')}
### Cloud

{#for note in notes('CLOUD')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('WEB')}
### Web

{#for note in notes('WEB')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('DATA')}
### Data et Intelligence Artificielle

{#for note in notes('DATA')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('TOOLING')}
### Outillage

{#for note in notes('TOOLING')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('ARCHITECTURE')}
### Architecture

{#for note in notes('ARCHITECTURE')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('METHODOLOGIES')}
### Méthodologies

{#for note in notes('METHODOLOGIES')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('SECURITY')}
### Sécurité

{#for note in notes('SECURITY')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('SOCIETY')}
### Loi, société et organisation

{#for note in notes('SOCIETY')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('TOOL_OF_THE_EPISODE')}
## Outils de l’épisode

{#for note in notes('TOOL_OF_THE_EPISODE')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

{#if hasNotes('BEGINNERS')}
## Rubrique débutant

{#for note in notes('BEGINNERS')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}
{/if}

## Conférences

{#for note in notes('CONFERENCES')}
{note.text}

{#for comment in note.comments}
{comment}
{/for}

{/for}

La liste des conférences provenant de [Developers Conferences Agenda/List](https://github.com/scraly/developers-conferences-agenda)
par [Aurélie Vache](https://github.com/scraly) et contributeurs :

{conferences.markdown(locale)}

## Nous contacter

Pour réagir à cet épisode, venez discuter sur le groupe Google <https://groups.google.com/group/lescastcodeurs>

Contactez-nous via X/twitter <https://twitter.com/lescastcodeurs> ou Bluesky <https://bsky.app/profile/lescastcodeurs.com>  
[Faire un crowdcast ou une crowdquestion](https://lescastcodeurs.com/crowdcasting/)  
Soutenez Les Cast Codeurs sur Patreon <https://www.patreon.com/LesCastCodeurs>  
Tous les épisodes et toutes les infos sur <https://lescastcodeurs.com/>
<!-- vim: set spelllang=fr : -->
