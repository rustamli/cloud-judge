
import webapp2
import logging
from ctrl_accounts import *
from ctrl_base import *

config = {"webapp2_extras.sessions": {
    "secret_key": "berlin8996", # another key used in production
}}

logging.getLogger().setLevel(logging.DEBUG)

application = webapp2.WSGIApplication([
    ("/", MainPage),
    ("/sign-in", SignInPage),
    ("/sign-up", SignUpPage),
    ("/sign-out", SignOutHandler),
    (r"/accounts/([a-zA-Z0-9-_]+)", AccountPage),
], config=config, debug=True)