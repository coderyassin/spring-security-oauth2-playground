# OAuth JS test app

Mini application HTML + JavaScript sans dépendances, servie sur le port 4200.

## Démarrer

Nécessite Node.js 18+ :

```bash
node server.mjs
```

Puis ouvrir http://localhost:4200.

## Configuration par défaut

- Backend : `http://localhost:8080`
- Démarrage Google : `/auth/google`
- Démarrage Keycloak : `/auth/keycloak`
- `clientApp` Keycloak : `app-a`
- Échange : `/auth/exchange` (POST JSON `{ "code": "..." }`)

Ces chemins sont des valeurs par défaut : adapte-les à tes mappings Spring réels.

## Redirect URI

L'application transmet `http://localhost:4200/` comme `redirectUri`. Ajoute exactement cette valeur à la liste autorisée côté backend (`RedirectUriValidator`).

Le backend doit rediriger vers cette URI avec le code, par exemple `http://localhost:4200/?code=...`.

## CORS

L'appel `fetch` vers le backend est cross-origin. En développement, autorise `http://localhost:4200` sur l'endpoint d'échange. La page utilise `credentials: "include"` pour permettre l'envoi des cookies de session si nécessaire.

Ne mets jamais le client secret OAuth dans cette page. Le code d'autorisation doit être à usage unique et expirer rapidement.
