import express from "express";
import path from "path";
import fs from "fs";
import { createServer as createViteServer } from "vite";

async function startServer() {
  const app = express();
  const PORT = 3000;

  app.use(express.json());

  // API health check
  app.get("/api/health", (_req, res) => {
    res.json({ status: "ok", timestamp: new Date().toISOString() });
  });

  // Serve APK files if present
  const serveApk = (res: express.Response, fileName: string) => {
    const publicPath = path.join(process.cwd(), "public", fileName);
    const distPath = path.join(process.cwd(), "dist", fileName);
    const rootPath = path.join(process.cwd(), fileName);
    
    if (fs.existsSync(publicPath)) {
      res.download(publicPath, fileName);
    } else if (fs.existsSync(distPath)) {
      res.download(distPath, fileName);
    } else if (fs.existsSync(rootPath)) {
      res.download(rootPath, fileName);
    } else {
      res.status(404).send("APK file not found on server.");
    }
  };

  app.get("/abhaya-kavach.apk", (_req, res) => {
    serveApk(res, "abhaya-kavach.apk");
  });

  app.get("/app-debug.apk", (_req, res) => {
    serveApk(res, "app-debug.apk");
  });

  // Vite middleware for development
  if (process.env.NODE_ENV !== "production") {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: "spa",
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), "dist");
    app.use(express.static(distPath));
    app.get("*all", (_req, res) => {
      res.sendFile(path.join(distPath, "index.html"));
    });
  }

  app.listen(PORT, "0.0.0.0", () => {
    console.log(`Server running on http://0.0.0.0:${PORT}`);
  });
}

startServer().catch((err) => {
  console.error("Failed to start server:", err);
  process.exit(1);
});
