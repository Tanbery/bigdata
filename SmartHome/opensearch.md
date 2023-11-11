Open Search commands: 

[Open Search Official Docs]: https://opensearch.org/docs/latest/im-plugin/index/


```bash
curl http://localhost:9200/_cluster/health


PUT <index>/_doc/<id>
{ "A JSON": "document" }

#bulk create index
POST _bulk
{ "index": { "_index": "<index>", "_id": "<id>" } }
{ "A JSON": "document" }

#create index with curl
curl -H "Content-Type: application/x-ndjson" -POST https://localhost:9200/data/_bulk -u 'admin:admin' --insecure --data-binary "@data.json"


PUT /movies
GET /movies


#create a new doc under index
POST movies/_doc
{ "title": "Spirited Away" }

#create a new doc with id under index
PUT movies/_doc/1
{ "title": "Spirited Away" }

#get a doc with id
GET movies/_doc/1

{
  "_index" : "movies",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 1,
  "_seq_no" : 0,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "title" : "Spirited Away"
  }
}

#get multiple doc
GET _mget
{
  "docs": [
    {
      "_index": "<index>",
      "_id": "<id>"
    },
    {
      "_index": "<index>",
      "_id": "<id>"
    }
  ]
}


GET _mget
{
  "docs": [
    {
      "_index": "<index>",
      "_id": "<id>",
      "_source": "field1"
    },
    {
      "_index": "<index>",
      "_id": "<id>",
      "_source": "field2"
    }
  ]
}


POST movies/_update/1
{
  "doc": {
    "title": "Castle in the Sky",
    "genre": ["Animation", "Fantasy"]
  }
}
DELETE /movies
```

