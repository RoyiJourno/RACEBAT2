using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using EF;

namespace Racebet.Controllers
{
    public class GroupsController : ApiController
    {

        // GET api/groups
        public Group Get(string id) {

            RacebetDBEntities db = new RacebetDBEntities();
            return Group g = db.Groups.Where(x => x.GroupID == id.ToString()).select;

        }
        


        // POST api/<controller>
        public void Post([FromBody]Group _group)
        {
            RacebetDBEntities db = new RacebetDBEntities();
            db.Group.Add(_group);
            db.SaveChanges();
        }
    }
}