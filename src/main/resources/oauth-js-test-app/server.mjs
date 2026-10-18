
import http from "node:http";
import { readFile } from "node:fs/promises";
import path from "node:path";
import { fileURLToPath } from "node:url";

const here = path.dirname(fileURLToPath(import.meta.url));
const port = Number(process.env.PORT || 4200);

const server = http.createServer(async (req, res) => {
  const pathname = new URL(
      req.url,
      `http://${req.headers.host || "localhost"}`
  ).pathname;

  // Page principale : test OAuth
  if (req.method === "GET" &&
      (pathname === "/" || pathname === "/index.html")) {
    try {
      const html = await readFile(path.join(here, "index.html"));

      res.writeHead(200, {
        "Content-Type": "text/html; charset=utf-8",
        "Cache-Control": "no-store",
        "X-Content-Type-Options": "nosniff"
      });

      res.end(html);
    } catch {
      res.writeHead(500, {
        "Content-Type": "text/plain; charset=utf-8"
      });
      res.end("Impossible de lire index.html");
    }

    return;
  }

  // Page de configuration du mot de passe
  if (req.method === "GET" && pathname === "/set-password") {
    try {
      const html = await readFile(
          path.join(here, "set-password.html")
      );

      res.writeHead(200, {
        "Content-Type": "text/html; charset=utf-8",
        "Cache-Control": "no-store",
        "X-Content-Type-Options": "nosniff",
        "Referrer-Policy": "no-referrer"
      });

      res.end(html);
    } catch {
      res.writeHead(500, {
        "Content-Type": "text/plain; charset=utf-8"
      });
      res.end("Impossible de lire set-password.html");
    }

    return;
  }

  res.writeHead(404, {
    "Content-Type": "text/plain; charset=utf-8"
  });
  res.end("Not found");
});

server.listen(port, "127.0.0.1", () => {
  console.log(`OAuth test app prête : http://localhost:${port}`);
});