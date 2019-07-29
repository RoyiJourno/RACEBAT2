using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using EF;

namespace Racebet.Controllers
{
    [RoutePrefix("api/users")]
    public class UsersController : ApiController
    {
        // GET api/<controller>
        [HttpGet]
        [Route("getusers")]
        public IEnumerable<string> Get(int id)
        {
            RacebetDBEntities db = new RacebetDBEntities();
            User u = db.Users.Where(x => x.UserID == id);

            if(u==null)
            {
                return Request.CreateResponse(HttpStatusCode.NotFound, " ");
            }
            return Request.CreateResponse(HttpStatusCode.OK, u.Name);
        }

        // GET api/<controller>/5
        public string Get(int id)
        {
            return "value";
        }

        // POST api/<controller>

        public void Post([FromBody]User _user)
        {
            RacebetDBEntities db = new RacebetDBEntities();
            db.Users.Add(_user);
            db.SaveChanges();
        }

        // PUT api/<controller>/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/<controller>/5
        public void Delete(int id)
        {
        }
    }
}