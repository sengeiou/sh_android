MODELS_DIR="${PROJECT_DIR}/Goles/CoreData"
DATA_MODEL_PACKAGE="$MODELS_DIR/golesDataBase.xcdatamodeld"
CURRENT_VERSION=`/usr/libexec/PlistBuddy "$DATA_MODEL_PACKAGE/.xccurrentversion" -c 'print _XCCurrentVersionName'`
OUTPUT_DIR="$MODELS_DIR/"

# Mogenerator Location
if [ -x /usr/local/bin/mogenerator ]; then
    echo "mogenerator exists in /usr/local/bin path";
    MOGENERATOR_DIR="/usr/local/bin";
elif [ -x /usr/bin/mogenerator ]; then
    echo "mogenerator exists in /usr/bin path";
    MOGENERATOR_DIR="/usr/bin";
else
    echo "mogenerator not found"; exit 1;
fi

# $MOGENERATOR_DIR/mogenerator --model "$DATA_MODEL_PACKAGE/$CURRENT_VERSION" --output-dir "$OUTPUT_DIR/"
$MOGENERATOR_DIR/mogenerator --model "$DATA_MODEL_PACKAGE/golesDataBase.xcdatamodel" --output-dir "$OUTPUT_DIR/" --template-var arc=true