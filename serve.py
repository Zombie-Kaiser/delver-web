import http.server
import os
import socketserver

PORT = 8138
ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "html", "webapp")


class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=ROOT, **kwargs)

    def end_headers(self):
        self.send_header("Cache-Control", "no-store")
        super().end_headers()


if __name__ == "__main__":
    with socketserver.ThreadingTCPServer(("127.0.0.1", PORT), Handler) as httpd:
        httpd.allow_reuse_address = True
        print("serving %s at http://127.0.0.1:%d" % (ROOT, PORT), flush=True)
        httpd.serve_forever()
