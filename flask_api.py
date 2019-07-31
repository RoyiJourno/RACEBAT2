import httplib2
import flask
from flask import request, jsonify
from sqlalchemy import create_engine, MetaData, Table, Column, ForeignKey, and_, text
from sqlalchemy.orm import sessionmaker, scoped_session
from sqlalchemy.ext.automap import automap_base
from sqlalchemy.orm.exc import NoResultFound
import json
from uuid import UUID

# Global http obj
h = httplib2.Http()


# Query serializers


def group_serializer(group):
    return dict(gid=group.gid, gname=group.gname, maxusers=group.maxusers, duration=str(group.duration),
                betprice=group.betprice, adminid=group.adminid)


def user_serializer(user):
    return dict(id=user.id, name=user.name, phone=user.phone, email=user.email,
                ppath=user.ppath)


def payment_serializer(payment):
    return dict(amount=payment.amount, out=payment.out, madeby=payment.madeby, forgroup=payment.forgroup)


# Get user's email from id_token header and check for token integrity


def auth_user(jwt):
<<<<<<< HEAD
    res, cont = h.request('https://www.googleapis.com/oauth2/v1/tokeninfo?id_token=' + jwt, method="GET",
                          headers={'accept': 'application/json'})
    # The below endpoint will return JSON formatted =>>>
    # =>>>id_token claims (such as email) after succesfull validation

    return {} if res.status != 200 else cont
=======
    req = h.request('https://www.googleapis.com/oauth2/v1/tokeninfo?id_token=' + jwt, method="GET",
                    headers={'accept': 'application/json'})
    # The below endpoint will return JSON formatted =>>>
    # =>>>id_token claims (such as email) after succesfull validation

    return json.loads(req[1])
>>>>>>> יאלה לסים


engine = create_engine("mysql+mysqlconnector://race:race@127.0.0.1/raceback")
connection = engine.connect()

Session = sessionmaker(bind=engine)

metadata = MetaData()
Base = automap_base()
Base.prepare(engine, reflect=True)

# Class assignment to var
<<<<<<< HEAD
Users = Base.classes.Users
Groups = Base.classes.Groups
Payments = Base.classes.Payments
=======
Users = Base.classes.users
Groups = Base.classes.groups
Payments = Base.classes.payments
>>>>>>> יאלה לסים
Users_Groups = Base.classes.usergroup

app = flask.Flask(__name__)


# Auth will be carried out on the first try section of the app =>>>
# =>>> validating a user through identity token integrity and signature validation =>>>
# =>>> and the existence of the user id in the DB. Also, nested try blocks exists to =>>>
# =>>> validate the existence of id key in the parameters.

# Added uuid4 id validation

@app.route('/group', methods=['POST', 'PUT', 'GET'])
def group():
<<<<<<< HEAD
    for item in request.form:
        if request.form[item] is None:
            return 400
    try:
        # UUID(request.form['id'], version=4)
        try:
            auth = auth_user(request.form['token'])
            if auth.__len__() == 0 or Users.query.filter(Users.mail == auth['email']).id != request.form['admin_id']:
=======
    if request.form['id'] is None:
        return "Invalid id", 400
    try:
        UUID(request.form['id'], version=4)
        try:
            auth = auth_user(request.form['token'])
            if auth.__len__() == 0 or Users.query.filter(Users.mail == json.loads(auth).email).id != request.form[
                'admin_id']:
>>>>>>> יאלה לסים
                return 'No permissions', 401
        except NoResultFound:
            return 'No permissions', 401
        if request.method == 'POST':
            group_to_insert = Groups()
            group_to_insert.gid = request.form['id']
            group_to_insert.gname = request.form['name']
            group_to_insert.duration = request.form['duration']
            group_to_insert.maxusers = int(request.form['max_users'])
            group_to_insert.betprice = int(request.form['bet_price'])
            group_to_insert.adminid = request.form['admin_id']
            db_session = scoped_session(sessionmaker(bind=engine))
            db_session.add(group_to_insert)
            db_session.commit()
            db_session.remove()
            return 200
        elif request.method == 'PUT':
            db_session = scoped_session(sessionmaker(bind=engine))
            id = request.form['id']
            upname = request.form['name']
            db_session.query(Groups).filter(Groups.gid == id).update({'gname': upname})
            db_session.commit()
            db_session.remove()
            return 200
        else:
            db_session = scoped_session(sessionmaker(bind=engine))
            id = request.form['id']
            q = db_session.query(Groups).filter(Groups.gid == id).first()
            res = group_serializer(q)
            db_session.remove()
            return 200
    except KeyError:
        return "Bad request", 400
    except ValueError:
        return "Invalid id", 400
<<<<<<< HEAD
    except TypeError:
        return "Invalid id", 400
=======
>>>>>>> יאלה לסים


@app.route('/user', methods=['POST', 'PUT', 'GET'])
def user():
    for item in request.form:
        if request.form[item] is None:
            return 400
    try:
        try:
            print(request.form['token'])
            auth = auth_user(request.form['token'])
<<<<<<< HEAD
            if request.method != 'POST':
                # TODO: AUTH IS NOT A LIST
                if auth.__len__() == 0 or Users.query.filter(Users.id == auth['email']).count() == 0:
                    return 'No permissions', 401
            else:
                if auth['email'] != request.form['email']:
                    return 'No permissions', 401
                if Users.query.filter(Users.id == request.form['email']).count() > 0:
                    return 409

=======
            print(auth)
            if request.method != 'POST':
                # TODO: AUTH IS NOT A LIST
                if auth._len_() == 0 or Users.query.filter(Users.id == json.loads(auth).email).id != request.form['id']:
                    return 'No permissions', 401
            else:
                if list(auth.keys()).__len__() == 0:
                    return 'No permissions', 401
>>>>>>> יאלה לסים
        except NoResultFound:
            return 'No permissions', 401
        if request.method == 'POST':

            user_to_insert = Users()
            user_to_insert.id = request.form['id']
            user_to_insert.name = request.form['name']
            user_to_insert.phone = request.form['phone']
            user_to_insert.ppath = request.form['ppath']
            user_to_insert.email = request.form['email']
            db_session = scoped_session(sessionmaker(bind=engine))
            db_session.add(user_to_insert)
            db_session.commit()
            db_session.remove()
            return '200, Ok'

        elif request.method == 'PUT':
            db_session = scoped_session(sessionmaker(bind=engine))
            id = request.form['id']
            if 'name' in request.form:
                upname = request.form['name']
                db_session.query(Users).filter(Users.id == id).update({'name': upname})
            if 'ppath' in request.form:
                ppath = request.form['ppath']
                db_session.query(Users).filter(Users.id == id).update({'ppath': ppath})
            db_session.commit()
            db_session.remove()
            return 200

        else:
            db_session = scoped_session(sessionmaker(bind=engine))
            id = request.form['id']
            query = db_session.query(Users).filter(Users.id == id).first()
            res = user_serializer(query)
            return json.dumps(res), 200
    except KeyError:
        return "Bad request", 400


# Post will add user to group
# Get will return list of groups that user is assinged to


@app.route('/groupofusers', methods=['GET', 'POST'])
def usertogroup():
    try:
        try:
            auth = auth_user(request.form['token'])
<<<<<<< HEAD
            if auth._len_() == 0 or Users.query.filter(Users.mail == json.loads(auth).email).id != request.form['id']:
=======
            if auth.len() == 0 or Users.query.filter(Users.mail == json.loads(auth).email).id != request.form['id']:
>>>>>>> יאלה לסים
                return 'No permissions', 401
        except NoResultFound:
            return 'No permissions', 401
        if request.method == 'POST':
            usergroup_add = Users_Groups()
            db_session = scoped_session(sessionmaker(bind=engine))
            user_id = request.form['id']
            group_id = request.form['gid']
            usergroup_add.id = user_id
            usergroup_add.gid = group_id
            db_session.add(usergroup_add)
            db_session.commit()
            db_session.remove()
            return 200
        else:
            id = request.form['id']
            query = Groups.query.join(Users).join(Groups).filter(Users.id == id).all()
            list_to_return = []
            for item in query:
                list_to_return.append(group_serializer(item))
            return json.dumps(list_to_return), 200
    except KeyError:
        return "Bad request", 400


# GET all users of a group
@app.route('/usersingroup', methods=['GET'])
def grouptousers():
    try:
        try:
            auth = auth_user(request.form['token'])
<<<<<<< HEAD
            if auth._len_() == 0 or Users.query.filter(Users.mail == json.loads(auth).email).id != request.form['id']:
=======
            if auth.len() == 0 or Users.query.filter(Users.mail == json.loads(auth).email).id != request.form['id']:
>>>>>>> יאלה לסים
                return 'No permissions', 401
        except NoResultFound:
            return 'No permissions', 401
        gid = request.form['gid']
        query = Users.query.join(Users).join(Groups).filter(Users.gid == id).all()
        list_to_return = []
        for item in query:
            list_to_return.append(user_serializer(item))
        return json.dumps(list_to_return), 200
    except KeyError:
        return "Bad request", 400


@app.route('/payments', methods=['GET'])
def payments():
    try:
        try:
            auth = auth_user(request.form['token'])
<<<<<<< HEAD
            if auth._len_() == 0 or Users.query.filter(Users.mail == json.loads(auth).email).id != request.form['id']:
=======
            if auth.len() == 0 or Users.query.filter(Users.mail == json.loads(auth).email).id != request.form['id']:
>>>>>>> יאלה לסים
                return 'No permissions', 401
        except NoResultFound:
            return 'No permissions', 401
            id = request.form['id']
            query = Payments.query.filter(Users.id == id).all()
            list_to_return = []
            for item in query:
                list_to_return.append(payment_serializer(item))
            return json.dumps(list_to_return), 200
    except KeyError:
        return "Bad request", 400

<<<<<<< HEAD

# GET all users of a group
@app.route('/signin', methods=['GET'])
def usersign():
    try:
        try:
            auth = auth_user(request.form['token'])
            if 0 == auth.__len__() or Users.query.filter(Users.id == auth['email']).count() == 0 or \
                    auth['email'] != request.form['email']:
                return 'No permissions', 401
        except NoResultFound:
            return 'No permissions', 401
        db_session = scoped_session(sessionmaker(bind=engine))
        email = request.form['email']
        query = db_session.query(Users).filter(Users.email == email).first()
        res = user_serializer(query)
        return json.dumps(res), 200
    except KeyError:
        return "Bad request", 400


=======
>>>>>>> יאלה לסים
if __name__ == "__main__":
    app.run(host="0.0.0.0",
            port=80)
