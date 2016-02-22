import cgi

#!/usr/bin/env python
"""
Very simple HTTP server in python.
Usage::
    ./dummy-web-server.py [<port>]
Send a GET request::
    curl http://localhost
Send a HEAD request::
    curl -I http://localhost
Send a POST request::
    curl -d "foo=bar&bin=baz" http://localhost
"""
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
import SocketServer

class S(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

    def do_GET(self):
        self._set_headers()
        self.wfile.write("<html><body><h1>Webserver</h1></body></html>")

    def do_HEAD(self):
        self._set_headers()
        
    def do_POST(self):
	self.send_response(200)
	self.send_header('Content-type', 'text/html')
	self.end_headers()
	ctype, pdict = cgi.parse_header(self.headers['content-type'])

        if ctype == 'multipart/form-data':
            postvars = cgi.parse_multipart(self.rfile, pdict)
        elif ctype == 'application/x-www-form-urlencoded':
            length = int(self.headers['content-length'])
            postvars = cgi.parse_qs(self.rfile.read(length), keep_blank_values=1)
        else:
            postvars = {}

	back = self.path if self.path.find('?') < 0 else self.path[:self.path.find('?')]

        try:
            # Write out the POST variables in 3 columns.
	    self.wfile.write('success %s\n' % postvars['isbn'])

	except:
	    self.wfile.write('failure\n')
        #    for key in postvars:
	#	self.wfile.write('%s : ' % key)
         #       self.wfile.write('%s\n' % postvars[key])



        #self.wfile.write('  </body>')
        #self.wfile.write('</html>')        


def run(server_class=HTTPServer, handler_class=S, port=8888):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)
    print 'Starting httpd...'
    httpd.serve_forever()

if __name__ == "__main__":
    from sys import argv

    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
