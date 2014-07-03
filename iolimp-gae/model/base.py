
from google.appengine.ext import db

class Account(db.Model):
    email = db.StringProperty()
    password = db.StringProperty()
    full_name = db.StringProperty()
    sign_up_date = db.DateTimeProperty()
    verified = db.BooleanProperty()
